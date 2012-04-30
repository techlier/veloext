/*
 * Copyright (c) 2012 Techlier Inc. All rights reserved.
 */
package jp.techlier.extensions.velocity.tools.generic;


/**
 *
 *
 * @since 2012/05/01
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 */
public class EscapeTool extends org.apache.velocity.tools.generic.EscapeTool {

    public String quote(final Object var) {
        return "\"" + var + "\"";
    }

    public String q(final Object var) {
        return quote(var);
    }

    public String singleQuote(final Object var) {
        return "'" + var + "'";
    }

    public String s(final Object var) {
        return singleQuote(var);
    }

}
