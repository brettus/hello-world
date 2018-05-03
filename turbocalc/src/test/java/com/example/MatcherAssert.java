package com.example;

import org.hamcrest.Matcher;


public class MatcherAssert {
	// standard versions from org.hamcrest.MatcherAssert
    public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
        org.hamcrest.MatcherAssert.assertThat(actual, matcher);
    }
    
    public static <T> void assertThat(String reason, T actual, Matcher<? super T> matcher) {
    	org.hamcrest.MatcherAssert.assertThat(reason, actual, matcher);
    }
    
    public static void assertThat(String reason, boolean assertion) {
    	org.hamcrest.MatcherAssert.assertThat(reason, assertion);
    }
    
    
    // param-swapped versions which take the messages last
    
    /**
	 * Asserts that a given matcher matches a given object.
	 * 
	 * @param actual
	 *            The object being tested
	 * @param matcher
	 *            The {@link Matcher} to test against the object
	 * @param reason
	 *            Explanation of why the matcher failed to match. E.g.,
	 *            "List did not contain expected contents". Should generally
	 *            only be used for complex asserts which don't read
	 *            "fluently enough", or where the matcher description is
	 *            expected to be long and verbose.
	 */
    public static <T> void assertThat(T actual, Matcher<? super T> matcher, String reason) {
    	org.hamcrest.MatcherAssert.assertThat(reason, actual, matcher);
    }
    
    public static void assertThat(boolean assertion, String reason) {
    	org.hamcrest.MatcherAssert.assertThat(reason, assertion);
    }
}
