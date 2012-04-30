/*
 * Copyright (c) 2010 Techlier Inc. All rights reserved.
 */
package jp.techlier.extensions.velocity.directive;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 */
public class NopTest extends DirectiveTestBase {

    @Test
    public void testNop() throws Exception {
        assertEquals("", eval("#nop()"));
    }

    @Test
    public void testNop_withArgument() throws Exception {
        String template = "#nop('arguments', 'are', 'ignored', $NULL)";
        assertEquals("", eval(template));
    }

}
