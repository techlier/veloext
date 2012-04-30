/*
 * Copyright (c) 2010 Techlier Inc. All rights reserved.
 */
package jp.techlier.extensions.velocity.directive;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 */
public class NilTest extends DirectiveTestBase {

    @Test
    public void testNil() throws Exception {
        String template = "#set($var = 'initial')"
                + "#nil()"
                + "このブロックは出力されないが、パースは行われる。"
                + "#set($var = 'value')"
                + "#end"
                + "$var";
        assertEquals("value", eval(template));
    }

    @Test
    public void testNil_withArguments() throws Exception {
        String template = "#nil('arguments', 'are', 'ignored', $NULL)"
                + "#nop()#end";
        assertEquals("", eval(template));
    }

}
