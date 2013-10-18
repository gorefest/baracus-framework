package net.mantucon.baracus.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.JUnit4;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 25.09.13
 * Time: 12:19
 * To change this template use File | Settings | File Templates.
 */
public class StringUtilTest {
    @Test
    public void testFirstByteToLower() throws Exception {
        Assert.assertEquals("doDo", StringUtil.firstByteToLower("DoDo"));
    }
}
