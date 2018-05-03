package com.example;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.example.models.Event;

public class IsEventWithTags extends TypeSafeDiagnosingMatcher<Event>
{
	private final List<String> expectedTags;
	private final boolean allowExtras;

	public IsEventWithTags(final boolean allowExtras, final List<String> expectedTags)
	{
		this.allowExtras = allowExtras;
		this.expectedTags = expectedTags;
		
		if(collectionContainsDuplicates(expectedTags))
		{
			throw new IllegalArgumentException("Input tag list must not contain duplicates");
		}
	}

	public IsEventWithTags(final boolean allowExtras, final String... expectedTags)
	{
		this(allowExtras, Arrays.asList(expectedTags));
	}

	@Override
	public void describeTo(final Description description)
	{
		description
			.appendText("Event with tags [")
			.appendText(formatTagList())
			.appendText("]");
	}

	@Override
	protected boolean matchesSafely(final Event item, final Description mismatchDescription)
	{
		final boolean hasAllTags = item.getTags().containsAll(this.expectedTags);
		final boolean hasExtraTags = item.getTags().size() > this.expectedTags.size();
		
		return (hasAllTags && (allowExtras || !hasExtraTags));
	}
	
	private String formatTagList()
	{
		return StringUtils.join(this.expectedTags.iterator(), ", ");
	}
	
	
	/**
	 * Checks to see if a {@link Collection} contains any duplicate items. Items
	 * <code>o1</code> and <code>o2</code> are considered duplicates if
	 * <code>o1.equals(o2)</code> returns <code>true</code>. <code>equals</code>
	 * and <code>hashCode</code> <em>must</em> follow the standard Java rules
	 * for this method to be reliable.
	 * 
	 * @param items
	 * @return True if the given list contains duplicate items, false otherwise.
	 */
	private <T> boolean collectionContainsDuplicates(final Collection<T> items)
	{
		final Set<T> uniqueItems = new HashSet<>(items);
		return uniqueItems.size() != items.size();
	}
	
	
	/**
	 * Creates a matcher for {@link Event}s which matches an Event that
	 * contains exactly the given set of tags.  The order of tags is not important.<br>
	 * <br>
	 * <strong>Examples:</strong><br>
	 * Suppose there is an Event named <code>event</code> which contains the tags <code>["alpha", "beta", "gamma"]</code>
	 * <br>
	 * These asserts would pass:<br>
	 * <ul>
	 * <li><code>assertThat(event, isEventWithTags("alpha", "beta", "gamma"));</code></li>
	 * <li><code>assertThat(event, isEventWithTags("gamma", "alpha", "beta"));</code></li>
	 * </ul>
	 * <br>
	 * These asserts would fail:<br>
	 * <ul>
	 * <li><code>
	 *     // missing the tag "gamma"<br>
	 *     assertThat(event, isEventWithTags("alpha", "beta"));<br>
	 *     &nbsp;
	 * </code></li>
	 * <li><code>
	 *     // has an extra tag not in the event<br>
	 *     assertThat(event, isEventWithTags("alpha", "beta", "gamma", "omega"));<br>
	 *     &nbsp;
	 * </code></li>
	 * <li><code>
	 *     // case doesn't match<br>
	 *     assertThat(event, isEventWithTags("Alpha", "beta", "gamma"));<br>
	 *     &nbsp;
	 * </code></li>
	 * <li><code>
	 *     // missing tag and extra tag<br>
	 *     assertThat(event, isEventWithTags("alpha", "beta", "omega"));<br>
	 *     &nbsp;
	 * </code></li>
	 * </ul>
	 * 
	 * @param expectedTags The set of tags to match.  This 
	 * @return A Matcher
	 * @throws IllegalArgumentException If the expected tags list contains duplicates.
	 */
	public static Matcher<Event> isEventWithTags(final String... expectedTags) throws IllegalArgumentException
	{
		return new IsEventWithTags(false, expectedTags);
	}
	
	/**
	 * Creates a matcher for {@link Event}s which matches an Event that
	 * contains exactly the given set of tags.  The order of tags is not important.<br>
	 * <br>
	 * <strong>Examples:</strong><br>
	 * Suppose there is an Event named <code>event</code> which contains the tags <code>["alpha", "beta", "gamma"]</code>
	 * <br>
	 * These asserts would pass:<br>
	 * <ul>
	 * <li><code>assertThat(event, is(eventWithTags("alpha", "beta", "gamma")));</code></li>
	 * <li><code>assertThat(event, is(eventWithTags("gamma", "alpha", "beta")));</code></li>
	 * </ul>
	 * <br>
	 * These asserts would fail:<br>
	 * <ul>
	 * <li><code>
	 *     // missing the tag "gamma"<br>
	 *     assertThat(event, is(eventWithTags("alpha", "beta")));<br>
	 *     &nbsp;
	 * </code></li>
	 * <li><code>
	 *     // has an extra tag not in the event<br>
	 *     assertThat(event, is(eventWithTags("alpha", "beta", "gamma", "omega")));<br>
	 *     &nbsp;
	 * </code></li>
	 * <li><code>
	 *     // case doesn't match<br>
	 *     assertThat(event, is(eventWithTags("Alpha", "beta", "gamma")));<br>
	 *     &nbsp;
	 * </code></li>
	 * <li><code>
	 *     // missing tag and extra tag<br>
	 *     assertThat(event, is(eventWithTags("alpha", "beta", "omega")));<br>
	 *     &nbsp;
	 * </code></li>
	 * </ul>
	 * 
	 * @param expectedTags The set of tags to match.  This 
	 * @return A Matcher
	 * @throws IllegalArgumentException If the expected tags list contains duplicates.
	 */
	public static Matcher<Event> eventWithTags(final String... expectedTags)
	{
		return new IsEventWithTags(false, expectedTags);
	}
	
	/**
	 * Creates a matcher for {@link Event}s which matches an Event that
	 * contains the given set of tags, and potentially others. The order of tags
	 * is not important.<br>
	 * <br>
	 * <strong>Examples:</strong><br>
	 * Suppose there is an Event named <code>event</code> which contains the
	 * tags <code>["alpha", "beta", "gamma"]</code> <br>
	 * These asserts would pass:<br>
	 * <ul>
	 * <li>
	 * <code>assertThat(event, isEventWithAtLeastTags("alpha", "beta", "gamma"));</code>
	 * </li>
	 * <li>
	 * <code>assertThat(event, isEventWithAtLeastTags("gamma", "alpha", "beta"));</code>
	 * </li>
	 * <li>
	 * <code>assertThat(event, isEventWithAtLeastTags("alpha", "beta"));</code></li>
	 * <li><code>assertThat(event, isEventWithAtLeastTags("gamma"));</code></li>
	 * </ul>
	 * <br>
	 * These asserts would fail:<br>
	 * <ul>
	 * <li><code>
	 *     // has an extra tag not in the event<br>
	 *     assertThat(event, isEventWithAtLeastTags("alpha", "beta", "gamma", "omega"));<br>
	 *     &nbsp;
	 * </code></li>
	 * <li><code>
	 *     // case doesn't match<br>
	 *     assertThat(event, isEventWithAtLeastTags("Alpha", "beta", "gamma"));<br>
	 *     &nbsp;
	 * </code></li>
	 * </ul>
	 * 
	 * @param expectedTags
	 *            The set of tags to match. This
	 * @return A Matcher
	 * @throws IllegalArgumentException
	 *             If the expected tags list contains duplicates.
	 */
	public static Matcher<Event> isEventWithAtLeastTags(final String... expectedTags) throws IllegalArgumentException
	{
		return new IsEventWithTags(true, expectedTags);
	}
	
	/**
	 * Creates a matcher for {@link Event}s which matches an Event that
	 * contains the given set of tags, and potentially others. The order of tags
	 * is not important.<br>
	 * <br>
	 * <strong>Examples:</strong><br>
	 * Suppose there is an Event named <code>event</code> which contains the tags <code>["alpha", "beta", "gamma"]</code>
	 * <br>
	 * These asserts would pass:<br>
	 * <ul>
	 * <li><code>assertThat(event, is(eventWithAtLeastTags("alpha", "beta", "gamma")));</code></li>
	 * <li><code>assertThat(event, is(eventWithAtLeastTags("gamma", "alpha", "beta")));</code></li>
	 * <li><code>assertThat(event, is(eventWithAtLeastTags("alpha", "beta")));</code></li>
	 * <li><code>assertThat(event, is(eventWithAtLeastTags("gamma")));</code></li>
	 * </ul>
	 * <br>
	 * These asserts would fail:<br>
	 * <ul>
	 * <li><code>
	 *     // has an extra tag not in the event<br>
	 *     assertThat(event, is(eventWithAtLeastTags("alpha", "beta", "gamma", "omega")));<br>
	 *     &nbsp;
	 * </code></li>
	 * <li><code>
	 *     // case doesn't match<br>
	 *     assertThat(event, is(eventWithAtLeastTags("Alpha", "beta", "gamma")));<br>
	 *     &nbsp;
	 * </code></li>
	 * </ul>
	 * 
	 * @param expectedTags The set of tags to match.  This 
	 * @return A Matcher
	 * @throws IllegalArgumentException If the expected tags list contains duplicates.
	 */
	public static Matcher<Event> eventWithAtLeastTags(final String... expectedTags)
	{
		return new IsEventWithTags(true, expectedTags);
	}
}
