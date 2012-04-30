/*
 * Copyright (c) 2010-2012 Techlier Inc. All rights reserved.
 */
package jp.techlier.extensions.velocity.directive;

import org.apache.velocity.exception.ParseErrorException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 */
public class DefvarTest extends DirectiveTestBase {

    @Test
    public void testDefvar() throws Exception {
        String template = "#defvar($var, 'value')"
                + "$var"
                + "#defvar($var, 'already defined')"
                + " is $var";
        assertEquals("value is value", eval(template));
    }

    @Test
    public void testDefvar_afterSet() throws Exception {
        String template = "#set($var = 'value')"
                + "$var"
                + "#defvar($var, 'already defined')"
                + " is $var";
        assertEquals("value is value", eval(template));
    }

    @Test
    public void testDefvar_andSet() throws Exception {
        String template = "#defvar($var, 'value')"
                + "$var "
                + "#set($var = 'changed')"
                + " is $var";
        assertEquals("value is changed", eval(template));
    }


    @Test(expected=ParseErrorException.class)
    public void testDefvar_withNoArgument() throws Exception {
        eval("#defvar()");
    }

    @Test(expected=ParseErrorException.class)
    public void testDefvar_withLessArgument() throws Exception {
        eval("#defvar($LESS)");
    }

    @Test(expected=ParseErrorException.class)
    public void testDefvar_withExcessArgument() throws Exception {
        eval("#defvar($EXCESS, 'value', 'excess')");
    }

    @Test(expected=ParseErrorException.class)
    public void testDefvar_withLiteralArgument() throws Exception {
        eval("#defvar('literal', 'value')");
    }

}
