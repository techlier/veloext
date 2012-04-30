/*
 * Copyright (c) 2010 Techlier Inc. All rights reserved.
 */
package jp.techlier.extensions.velocity.directive;

import org.apache.velocity.exception.ParseErrorException;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 */
public class DisplaceTest extends DirectiveTestBase {

    @Test
    public void testDisplace_withLiteral() throws Exception {
        testDisplace("'/test.vm'", "Here is 'test.vm'.\r\n");
    }

    @Test
    public void testDisplace_withReference() throws Exception {
        context_.put("file", "/test.vm");
        testDisplace("$file", "Here is 'test.vm'.\r\n");
    }

    @Test
    public void testDisplace_notExists() throws Exception {
        testDisplace("'test.vm'", null);
        testDisplace("$NULL", null);
    }

    @Test(expected=ParseErrorException.class)
    public void testDisplace_withNoArgument() throws Exception {
        testDisplace("", null);
    }


    void testDisplace(String templateName, String expected) throws Exception {
        String body;
        if (expected == null) {
            expected = body = "This　text is not displaced.";
        }
        else {
            body = "This　text will be displaced.";
        }
        String template = "#displace("+templateName+")"
                + body
                + "#end";
        assertEquals(expected, eval(template));
    }

}
