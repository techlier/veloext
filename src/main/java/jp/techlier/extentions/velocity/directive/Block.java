/**
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
package jp.techlier.extentions.velocity.directive;

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
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import jp.techlier.extentions.velocity.directive.Nil.NilWriter;


/**
 * Pluggable directive that handles the <code>#block()</code> statement.
 * <p>
 * 再定義可能なブロックを定義する。
 * applyディレクティブと組み合わせることで、テンプレートの部分的な差し替えを可能とする。
 * </p>
 * <p>
 * プロパティ<code>directive.block.late.rendering</code>によってパースのタイミングを変更可能。
 * </p>
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @version $Id: Block.java 9 2010-09-12 10:07:34Z kazuhide $
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
     * #apply('base.vm')
     * #set($var1 = 'extended')
     * #block('block')
     * $var1 = extended
     * $!var2 = undefined
     * #end
     * #end
     *
     * base.vm...
     * #set($var1 = 'base')
     * #set($var2 = 'defined')
     * $var1 = base
     * $!var2 = defined
     * #block('block')
     * $var1 = base
     * $!var2 = defined
     * #end
     *
     * output if directive.block.late.rendering = true...
     * base = base
     * defined = defined
     * base = extended
     * defined = undefined
     *
     * output if directive.block.late.rendering = false...
     * base = base
     * defined = defined
     * extended = extended
     *  = undefined
     * </pre>
     *　</p>
     */
    public static final String LATE_RENDERING = "directive.block.late.rendering";

    private static final boolean DEFAULT_LATE_RENDERING = false;


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

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#render(org.apache.velocity.context.InternalContextAdapter, java.io.Writer, org.apache.velocity.runtime.parser.node.Node)
     */
    @Override
    public boolean render(final InternalContextAdapter context,
                          final Writer writer,
                          final Node node)
            throws IOException, ResourceNotFoundException,
                   ParseErrorException, MethodInvocationException {
        final List<Object> chainOfBlock = getBlockChain(context, node);
        if (chainOfBlock == null) return false;

        boolean result = true;
        if (writer instanceof NilWriter) { // inside apply derective
            if (chainOfBlock.contains(BASEBLOCK_RENDERING_POSITION_MARKER)) {
                final Object blockObject = parseBlock(context, node);
                if (blockObject != null) {
                    chainOfBlock.set(chainOfBlock.indexOf(BASEBLOCK_RENDERING_POSITION_MARKER), blockObject);
                }
                else {
                    result = false;
                }
            }
        }
        else {
            for (final Object blockObject: chainOfBlock) {
                if (blockObject == BASEBLOCK_RENDERING_POSITION_MARKER) {
                    result &= node.jjtGetChild(1).render(context, writer);
                }
                else if (blockObject instanceof Node) {
                    result &= ((Node)blockObject).render(context, writer);
                }
                else {
                    writer.write(blockObject.toString());
                }
            }
        }
        return result;
    }

    protected Object parseBlock(final InternalContextAdapter context, final Node node)
            throws MethodInvocationException, ParseErrorException,
                   ResourceNotFoundException, IOException {
        final boolean renderingLater = rsvc.getBoolean(LATE_RENDERING, DEFAULT_LATE_RENDERING);
        if (renderingLater) {
            return node.jjtGetChild(1);
        }
        else {
            final StringWriter blockWriter = new StringWriter();
            if (!node.jjtGetChild(1).render(context, blockWriter)) {
                return null;
            }
            return blockWriter.toString();
        }
    }

    protected List<Object> getBlockChain(final InternalContextAdapter context, final Node node) {
        final DirectiveUtils util = new DirectiveUtils(this, rsvc, context, node);
        if (node.jjtGetNumChildren() != 2) {
            util.error("invalid argument count");
            return null;
        }

        final String blockName = util.getStringArgument(0);
        if (blockName == null) {
            util.warn("null blockname is appeared");
            return null;
        }

        @SuppressWarnings("unchecked")
        Map<String,List<Object>> blockRefs = (Map<String,List<Object>>)context.get(Block.class.getName());
        if (blockRefs == null) {
            context.put(Block.class.getName(), blockRefs = new HashMap<String,List<Object>>());
        }
        if (blockRefs.containsKey(blockName)) {
            return blockRefs.get(blockName);
        }
        else {
            final List<Object> newBlock = new ArrayList<Object>();
            newBlock.add(BASEBLOCK_RENDERING_POSITION_MARKER);
            blockRefs.put(blockName, newBlock);
            return newBlock;
        }
    }

    protected static final Object BASEBLOCK_RENDERING_POSITION_MARKER = new Object();

}
