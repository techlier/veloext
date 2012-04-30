/*
 * Copyright (c) 2010-2012 Techlier Inc. All rights reserved.
 */
package jp.techlier.extensions.velocity.directive;

import org.apache.velocity.exception.ParseErrorException;

import org.junit.Test;


/**
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 * @since 2010/09/10
 */
public class BlockTest extends DirectiveTestBase {

    @Test
    public void testBlock_withNullArgument() throws Exception {
        eval("#block($NULL)#nop()#end");
    }


    @Test(expected=ParseErrorException.class)
    public void testBlock_withNoArgument() throws Exception {
        eval("#block()#nop()#end");
    }

    @Test(expected=ParseErrorException.class)
    public void testBlock_withExcessArgument() throws Exception {
        eval("#block('dummy', 'excess')#nop()#end");
    }


    @Test
    public void testcase1_1() throws Exception {
        testExample("test1-extend1.vm");
    }

    @Test
    public void testcase1_2() throws Exception {
        testExample("test1-extend2.vm");
    }

}
