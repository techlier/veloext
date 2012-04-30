/*
 * Copyright (c) 2010 Techlier Inc. All rights reserved.
 */
package jp.techlier.extensions.velocity.directive;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 */
public class ImportTest extends DirectiveTestBase {

    @Test
    public void testImport() throws Exception {
        String template = "#import('/import.vm')";
        String expected = "Before importing 'test.vm'.\r\n"
                        + "Here is 'test.vm'.\r\n"
                        + "After importing 'test.vm'.\r\n";
        assertEquals(expected, eval(template));
    }

    @Test(expected=ResourceNotFoundException.class)
    public void testImport_notFound() throws Exception {
        assertEquals("", eval("#import('import.vm')"));
    }

    @Test
    public void testImport_withNullArgument() throws Exception {
        eval("#import($NULL)");
    }


    @Test(expected=ParseErrorException.class)
    public void testImport_withNoArgument() throws Exception {
        eval("#import()");
    }

    @Test(expected=ParseErrorException.class)
    public void testImport_withExcessArgument() throws Exception {
        eval("#import('dummy.vm', 'excess')");
    }

}
