/*
 * Copyright (c) 2012 Techlier Inc. All rights reserved.
 */
package jp.techlier.extensions.velocity.directive;

import org.junit.Test;


/**
 * Test template examples written in javadoc comment.
 *
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 */
public class ExamplesTest extends DirectiveTestBase {

    public static final String EXAMPLES_DIR = "examples/";

    @Test
    public void testDefvar() throws Exception {
        testExample(EXAMPLES_DIR+"defvar-example.vm");
    }

    @Test
    public void testDefconst() throws Exception {
        testExample(EXAMPLES_DIR+"defconst-example.vm");
    }

    @Test
    public void testApply() throws Exception {
        testExample(EXAMPLES_DIR+"apply-example.vm");
    }

    @Test
    public void testBlock_withoutLateRendering() throws Exception {
        //assertEquals(engine_.getProperty(Block.LATE_RENDERING), false):
        testExample(EXAMPLES_DIR+"block-example-extend.vm",
                    EXAMPLES_DIR+"block-example.late=false.txt");
    }

    @Test
    public void testBlock_withLateRendering() throws Exception {
        engine_.setProperty("directive.block.late.rendering", true);
        testExample(EXAMPLES_DIR+"block-example-extend.vm",
                    EXAMPLES_DIR+"block-example.late=true.txt");
    }

    @Test
    public void testNil() throws Exception {
        testExample(EXAMPLES_DIR+"nil-example.vm");
    }

    @Test
    public void testPrepend() throws Exception {
        testExample(EXAMPLES_DIR+"prepend-example.vm");
    }

    @Test
    public void testAppend() throws Exception {
        testExample(EXAMPLES_DIR+"append-example.vm");
    }

}
