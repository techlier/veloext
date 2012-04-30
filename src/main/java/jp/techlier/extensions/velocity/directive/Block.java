/*
 * Directive extensions for Apache Velocity.
 * Copyright (c) 2010 Techlier Inc. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jp.techlier.extensions.velocity.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import jp.techlier.extensions.velocity.directive.Apply.ApplyWriter;

import static jp.techlier.extensions.velocity.directive.DirectiveConstants.*;


/**
 * Pluggable directive that handles the <code>#block()</code> statement.
 * <p>
 * 再定義可能なブロックを定義する。
 * applyディレクティブと組み合わせることで、テンプレートの部分的な差し替えを可能とする。
 * </p>
 * <p>
 * プロパティ<code>directive.block.late.rendering</code>によってパースのタイミングを変更可能。
 * </p>
 *
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @since 1.0
 */
public class Block extends Directive {

    /**
     * #block()の展開タイミングを指定する(boolean)。
     * <p>
     * <pre>
     * true) 上書きされるblockが出現した時点のcontextに基づいて展開を行う。
     * false) 上書きするblockが出現した時点のcontextに基づいて展開を行う。[default]
     * </pre>
     * <b>記述例）</b>
     * <pre>
     * template...
     * #set($file = 'block-example-extend.vm')
     * #apply('block-example-base.vm')
     * #block('block1')
     * #set($var1 = 'extended')
     * #defvar($var2, 'extended')
     * block1@extend:
     *   file = $file
     *   var0 = $var0
     *   var1 = $var1
     *   var2 = $var2
     * #end
     * #end
     *
     * base.vm...
     * #set($file = 'block-example-base.vm')
     * #set($var0 = 'defined')
     * #set($var1 = 'base')
     * #defvar($var2, 'base')
     * #block('block0')
     * block0@base:
     *   file = $file
     *   var0 = $var0
     *   var1 = $var1
     *   var2 = $var2
     * #end
     * #block('block1')
     * block1@base:
     * #end
     * #block('block2')
     * block2@base:
     *   file = $file
     *   var0 = $var0
     *   var1 = $var1
     *   var2 = $var2
     * #end
     *
     * output if directive.block.late.rendering = false(default)...
     * block0@base:
     *   file = block-example-base.vm
     *   var0 = defined
     *   var1 = base
     *   var2 = extended
     * block1@extend:
     *   file = block-example-extend.vm
     *   var0 = $var0
     *   var1 = extended
     *   var2 = extended
     * block2@base:
     *   file = block-example-base.vm
     *   var0 = defined
     *   var1 = base
     *   var2 = extended
     *
     * output if directive.block.late.rendering = true...
     * block0@base:
     *   file = block-example-base.vm
     *   var0 = defined
     *   var1 = base
     *   var2 = base
     * block1@extend:
     *   file = block-example-base.vm
     *   var0 = defined
     *   var1 = extended
     *   var2 = base
     * block2@base:
     *   file = block-example-base.vm
     *   var0 = defined
     *   var1 = extended
     *   var2 = base
     * </pre>
     *　</p>
     */


    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getName()
     */
    @Override
    public String getName() {
        return "block";
    }

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getType()
     */
    @Override
    public int getType() {
        return BLOCK;
    }


    protected DirectiveHelper helper_;

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#init(org.apache.velocity.runtime.RuntimeServices, org.apache.velocity.context.InternalContextAdapter, org.apache.velocity.runtime.parser.node.Node)
     */
    @Override
    public void init(final RuntimeServices rs, final InternalContextAdapter context, final Node node)
            throws TemplateInitException {
        super.init(rs, context, node);

        helper_ = new DirectiveHelper(this, rsvc, context, node);
        if (node.jjtGetNumChildren() != 2) {
            throw helper_.newTemplateInitException(node, "requires exactly one argument.");
        }
        helper_.checkArgumentMustBeString(0);
    }

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#render(org.apache.velocity.context.InternalContextAdapter, java.io.Writer, org.apache.velocity.runtime.parser.node.Node)
     */
    @Override
    public boolean render(final InternalContextAdapter context,
                          final Writer writer,
                          final Node node)
            throws IOException, ResourceNotFoundException,
                   ParseErrorException, MethodInvocationException {
        final List<Object> blockChain = getBlockChain(context, node);
        if (blockChain == null) return false;

        boolean result = true;
        if (writer instanceof ApplyWriter) { // inside apply derective
            if (blockChain.contains(BASEBLOCK_RENDERING_POSITION_MARKER)) {
                final Object blockObject = parseBlock(context, node.jjtGetChild(1));
                if (blockObject != null) {
                    blockChain.set(blockChain.indexOf(BASEBLOCK_RENDERING_POSITION_MARKER), blockObject);
                }
                else {
                    result = false;
                }
            }
        }
        else {
            for (final Object blockObject: blockChain) {
                if (blockObject == BASEBLOCK_RENDERING_POSITION_MARKER) {
                    result &= node.jjtGetChild(1).render(context, writer);
                }
                else if (blockObject instanceof Node) {
                    result &= ((Node)blockObject).render(context, writer);
                }
                else {
                    writer.write(blockObject.toString());
                }
                if (!context.getAllowRendering()) {
                    break;
                }
            }
        }
        return result;
    }

    protected Object parseBlock(final InternalContextAdapter context, final Node blockNode)
            throws MethodInvocationException, ParseErrorException,
                   ResourceNotFoundException, IOException {
        final boolean renderingLater = rsvc.getBoolean(BLOCK_LATE_RENDERING, DEFAULT_BLOCK_LATE_RENDERING);
        if (renderingLater) {
            return blockNode;
        }
        else {
            final StringWriter blockWriter = new StringWriter();
            if (!blockNode.render(context, blockWriter)) {
                return null;
            }
            return blockWriter.toString();
        }
    }

    protected List<Object> getBlockChain(final InternalContextAdapter context, final Node node) {
        final String blockName = helper_.getStringArgument(0);
        if (blockName == null) {
            helper_.error("blockname must not be null.");
            return null;
        }
        helper_.debug(blockName);

        @SuppressWarnings("unchecked")
        Map<String,List<Object>> blockRefs = (Map<String,List<Object>>)context.get(Block.class.getName());
        if (blockRefs == null) {
            context.put(Block.class.getName(), blockRefs = new HashMap<String,List<Object>>());
        }

        List<Object> blockChain = blockRefs.get(blockName);
        if (blockChain == null || blockChain.isEmpty()) {
            blockChain = new ArrayList<Object>();
            blockChain.add(BASEBLOCK_RENDERING_POSITION_MARKER);
            blockRefs.put(blockName, blockChain);
        }
        return blockChain;
    }

    protected static final Object BASEBLOCK_RENDERING_POSITION_MARKER = new Object();

}
