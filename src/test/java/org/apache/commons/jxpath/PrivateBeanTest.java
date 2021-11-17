package org.apache.commons.jxpath;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PrivateBeanTest {
    private JXPathContext context;
    private SomeTestPOJOClass subject;

    @Before
    public void setup() throws Exception {
        subject = new SomeTestPOJOClass();
        context = JXPathContext.newContext(subject);
    }

    @Test
    public void beanPropertyDescriptorTest() {
        boolean someBool = (boolean) context.getValue("someBoolean");
        Assert.assertFalse(someBool);
    }
}

class SomeTestPOJOClass {
    private int i = 0;
    private String s = "";
    private double d = 42.0;
    private boolean someBoolean = false;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    private double getD() {
        return d;
    }
}
