/*
 * Copyright (c) 2010-2012 Techlier Inc. All rights reserved.
 */
package jp.techlier.extensions.velocity.directive;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.JdkLogChute;

import jp.techlier.extensions.velocity.directive.DirectiveUtils;

import org.junit.Before;

import junit.framework.Assert;



/**
 * @author <a href="mailto:okamura@techlier.jp">Kz Okamura</a>
 * @since 1.0
 */
public abstract class DirectiveTestBase {

    protected VelocityEngine engine_;
    protected VelocityContext context_;

    protected static final String TEMPLATES_DIR = "target/test-classes/templates/";


    @Before
    public void setUp() throws Exception {
        engine_ = new VelocityEngine();
        engine_.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
        engine_.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
        engine_.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, JdkLogChute.class.getName());
        engine_.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        engine_.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, TEMPLATES_DIR);

        DirectiveUtils.addUserDirectives(engine_);

        context_ = new VelocityContext();
    }


    protected String eval(String template) throws Exception {
        String classPath = getClass().getName().replace('.', '/');
        StringWriter writer = new StringWriter();
        engine_.evaluate(context_, writer, classPath, template);
        return writer.toString();
    }

    protected String parse(final String templateName) throws Exception {
        Template template = engine_.getTemplate(templateName, "UTF-8");
        StringWriter writer = new StringWriter(); try {
            template.merge(context_, writer);
        } finally {
            writer.flush();
            writer.close();
        }
        return writer.toString();
    }


    protected void testExample(final String templateName) throws Exception {
        testExample(templateName, templateName.replace(".vm", ".txt"));
    }

    protected void testExample(final String templateName, final String resultName) throws Exception {
        Assert.assertEquals(readStringFromFile(resultName), parse(templateName));
    }

    private String readStringFromFile(String fileName) throws IOException {
        if (fileName.charAt(0) != '/') {
            fileName = TEMPLATES_DIR + fileName;
        }
        InputStream in = new FileInputStream(fileName); try {
            return readString(in);
        } finally {
            in.close();
        }
    }

    private String readString(InputStream in) throws IOException {
        byte[] buf = new byte[1024];
        OutputStream out = new ByteArrayOutputStream();
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        return out.toString();
    }
}
