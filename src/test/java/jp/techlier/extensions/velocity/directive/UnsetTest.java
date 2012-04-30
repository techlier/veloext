/*
 * Copyright (c) 2010-2012 Techlier Inc. All rights reserved.
 */
package jp.techlier.extensions.velocity.directive;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 */
public class UnsetTest extends DirectiveTestBase {

    @Test
    public void testUnset() throws Exception {
        String template = "#set($val = 'value')"
                + "$val"
                + "#unset($val)"
                + " is $val";
        assertEquals("value is $val", eval(template));
    }

    @Test
    public void testUnset_hasInnerContext() throws Exception {
        context_.put("val", "innerValue");
        context_ = new VelocityContext(context_);
        String template = "#set($val = 'value')"
                + "$val"
                + "#unset($val)"
                + " is $val";
        assertEquals("value is innerValue", eval(template));

        template = "#set($val = 'value')"
                + "$val"
                + "#unset($val, true)"
                + " is $val";
        assertEquals("value is $val", eval(template));
    }


    @Test(expected=ParseErrorException.class)
    public void testUnset_withNoArgument() throws Exception {
        eval("#unset()");
    }

    @Test(expected=ParseErrorException.class)
    public void testUnset_withExcessArgument() throws Exception {
        eval("#unset($EXCESS, $flag, 'excess')");
    }

}
