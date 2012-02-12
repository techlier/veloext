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

import java.io.Writer;

import jp.techlier.commons.lang.reflect.ReflectUtils;

import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.context.InternalContextAdapterImpl;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.Node;


/**
 * Pluggable directive that handles the <code>#defconst()</code> statement.
 * <p>
 * 代入不可能な定数値を定義する。
 * </p>
 *　<p>
 * <b>記述例）</b>
 * <pre>
 * template...
 * #defconst($CONSTANT, 'immutable')
 * #set($CONSTANT = 'this statement is not effective')
 * $CONSTANT
 *
 * output...
 * immutable
 * </pre>
 *　</p>
 *
 * @author <a href="mailto:okamura@techlier.jp">Kazuhide 'Kz' Okamura</a>
 * @version $Id: Defconst.java 9 2010-09-12 10:07:34Z kazuhide $
 */
public class Defconst extends Directive {

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getName()
     */
    @Override
    public String getName() {
        return "defconst";
    }

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#getType()
     */
    @Override
    public int getType() {
        return LINE;
    }

    /*(non-Javadoc)
     * @see org.apache.velocity.runtime.directive.Directive#render(org.apache.velocity.context.InternalContextAdapter, java.io.Writer, org.apache.velocity.runtime.parser.node.Node)
     */
    @Override
    public boolean render(final InternalContextAdapter context,
                          final Writer writer,
                          final Node node) {
        final DirectiveUtils util = new DirectiveUtils(this, rsvc, context, node);

        if (node.jjtGetNumChildren() != 2) {
            return util.error("invalid argument count");
        }

        final Node referenceNameNode = node.jjtGetChild(0);
        if (referenceNameNode.getType() != ParserTreeConstants.JJTREFERENCE) {
            return util.error("first argument must be a reference");
        }
        final String referenceName = ((ASTReference)referenceNameNode).getRootString();
        if (context.get(referenceName) == null) {
            attachContextValueHandlingContext(context);
            context.put(referenceName,
                        new ConstantValue(node.jjtGetChild(1).value(context)));
        }
        return true;
    }


    /**
     * @param context
     */
    private void attachContextValueHandlingContext(final InternalContextAdapter context) {
        if (context == null) return;
        if (context instanceof InternalContextAdapterImpl) {
            Object obj = ReflectUtils.get(context, "context");
            if (!(obj instanceof ConstantValueHandlingContext)) {
                ReflectUtils.set(context, "context",
                                 new ConstantValueHandlingContext(rsvc, (Context)obj));
            }
        }
        else if (context != context.getBaseContext()) {
            attachContextValueHandlingContext(context.getBaseContext());
        }
    }


    private static class ConstantValue {
        final Object value_;

        public ConstantValue(final Object value) {
            value_ = value;
        }

        @Override
        public String toString() {
            return value_.toString();
        }
    }

    private static class ConstantValueHandlingContext implements Context {
        private final RuntimeServices runtimeServices_;
        private final Context context_;

        ConstantValueHandlingContext(RuntimeServices runtimeServices, Context context) {
            this.runtimeServices_ = runtimeServices;
            this.context_ = context;
        }

        /*(non-Javadoc)
         * @see org.apache.velocity.context.Context#put(java.lang.String, java.lang.Object)
         */
        @Override
        public Object put(String key, Object value) {
            if (context_.get(key) instanceof ConstantValue) {
                runtimeServices_.getLog().debug("cannot overwrite constant value: " + key);
                return null;
            }
            return context_.put(key, value);
        }

        /*(non-Javadoc)
         * @see org.apache.velocity.context.Context#get(java.lang.String)
         */
        @Override
        public Object get(String key) {
            Object value = context_.get(key);
            if (value instanceof ConstantValue) {
                value = ((ConstantValue)value).value_;
            }
            return value;
        }

        /*(non-Javadoc)
         * @see org.apache.velocity.context.Context#containsKey(java.lang.Object)
         */
        @Override
        public boolean containsKey(Object key) {
            return context_.containsKey(key);
        }

        /*(non-Javadoc)
         * @see org.apache.velocity.context.Context#getKeys()
         */
        @Override
        public Object[] getKeys() {
            return context_.getKeys();
        }

        /*(non-Javadoc)
         * @see org.apache.velocity.context.Context#remove(java.lang.Object)
         */
        @Override
        public Object remove(Object key) {
            if (context_.get(key.toString()) instanceof ConstantValue) {
                runtimeServices_.getLog().debug("cannot remove constant value: " + key);
                return null;
            }
            return context_.remove(key);
        }
    }

}
