/*
 * Copyright (c) 2010-2012 Techlier Inc. All rights reserved.
 */
package jp.techlier.extensions.velocity.directive;

import org.junit.*;
import static org.junit.Assert.assertEquals;


/**
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 */
public class DefconstTest extends DirectiveTestBase {

    @Test
    public void testDefconst() throws Exception {
        String template = "#defconst($var, 'immutable')"
                + "$var "
                + "#set($var = ' cannot change')"
                + " is $var";
        assertEquals("immutable is immutable", eval(template));
    }

    @Test
    public void testDefconst_andUnset() throws Exception {
        String template = "#defconst($var, 'immutable')"
                + "$var"
                + "#unset($var)"
                + " is $var";
        assertEquals("immutable is immutable", eval(template));
    }

    @Test
    public void testDefconst_stopByStep() throws Exception {
        eval("#defconst($CONSTANT, 'immutable')");
        assertEquals("immutable", eval("$CONSTANT"));

        eval("#set($CONSTANT = 'other context')");
        assertEquals("other context", eval("$CONSTANT"));
    }

}
