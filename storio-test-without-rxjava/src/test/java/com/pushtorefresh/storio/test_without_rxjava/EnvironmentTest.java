package com.pushtorefresh.storio.test_without_rxjava;

import com.pushtorefresh.storio.internal.Environment;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class EnvironmentTest {

    @Test
    public void noRxJavaInClassPath() {
        assertFalse(Environment.IS_RX_JAVA_AVAILABLE);
    }

    @Test(expected = ClassNotFoundException.class)
    public void rxJavaIsReallyNotInClassPath() throws ClassNotFoundException {
        Class.forName("rx.Observable");
    }
}
