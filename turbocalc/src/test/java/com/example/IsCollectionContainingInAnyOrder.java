package com.example;

import static org.hamcrest.core.IsEqual.equalTo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class IsCollectionContainingInAnyOrder<T> extends TypeSafeDiagnosingMatcher<Collection<? extends T>>
{
	private final Collection<Matcher<? super T>> matchers;
	
	public IsCollectionContainingInAnyOrder(final Collection<Matcher<? super T>> matchers)
	{
		this.matchers = matchers;
	}
	
	@Override
	protected boolean matchesSafely(final Collection<? extends T> items, final Description mismatchDescription)
	{
		final Matching<T> matching = new Matching<T>(matchers, mismatchDescription);
		for (final T item : items)
		{
			if (!matching.matches(item))
			{
				return false;
			}
		}
		
		return matching.isFinished(items);
	}
	
	@Override
	public void describeTo(final Description description)
	{
		description
			.appendText("Collection containing ")
			.appendList("[", ", ", "]", matchers)
			.appendText(" in any order");
	}
	
	private static class Matching<S>
	{
		private final Collection<Matcher<? super S>> matchers;
		private final Description mismatchDescription;
		
		public Matching(final Collection<Matcher<? super S>> matchers, final Description mismatchDescription)
		{
			this.matchers = new ArrayList<Matcher<? super S>>(matchers);
			this.mismatchDescription = mismatchDescription;
		}
		
		public boolean matches(final S item)
		{
			return isNotSurplus(item) && isMatched(item);
		}
		
		public boolean isFinished(final Collection<? extends S> items)
		{
			if (matchers.isEmpty())
			{
				return true;
			}
			
			mismatchDescription
				.appendText("The following were not found: ")
				.appendList("", ", ", "", matchers)
				.appendText(" in ")
				.appendValueList("[", ", ", "]", items);
			return false;
		}
		
		private boolean isNotSurplus(final S item)
		{
			if (matchers.isEmpty())
			{
				mismatchDescription
					.appendText("Not matched: ")
					.appendValue(item);
				return false;
			}
			return true;
		}
		
		private boolean isMatched(final S item)
		{
			for (final Matcher<? super S> matcher : matchers)
			{
				if (matcher.matches(item))
				{
					matchers.remove(matcher);
					return true;
				}
			}
			
			mismatchDescription
				.appendText("Found extra item: ")
				.appendValue(item);
			return false;
		}
		
	}
	
	@Factory
	public static <T> Matcher<Collection<? extends T>> collectionContainingInAnyOrder(Collection<? extends T> items)
	{
		final List<Matcher<? super T>> matchers = new ArrayList<Matcher<? super T>>();
		for (T item : items)
		{
			matchers.add(equalTo(item));
		}
		
		return new IsCollectionContainingInAnyOrder<T>(matchers);
	}
	
	@Factory
    public static <T> Matcher<Collection<? extends T>> collectionContainingMatchersInAnyOrder(final Collection<Matcher<? super T>> itemMatchers) {
		return new IsCollectionContainingInAnyOrder<T>(itemMatchers);
    }
	
	/**
	   * Matches a collection which contains the given items, and only the given items (i.e., if the collection 
	   * is missing any items, or if it contains any extra items, the match will fail). The items may exist 
	   * in the collection in any order.
	   * 
	   * <hr>
	   * 
	   * Creates an order agnostic matcher for {@link Collection}s that matches when a single pass over
	   * the examined {@link Collection} yields a series of items, each logically equal to one item
	   * anywhere in the specified items. For a positive match, the examined iterable
	   * must be of the same length as the number of specified items.
	   * <p/>
	   * N.B. each of the specified items will only be used once during a given examination, so be
	   * careful when specifying items that may be equal to more than one entry in an examined
	   * iterable.
	   * <p/>
	   * For example:
	   * <pre>
	   * Set&lt;String&gt; items = new HashSet<String>(Arrays.asList("bar", "foo"));
	   * assertThat(Arrays.asList("foo", "bar"), isCollectionContainingInAnyOrder(items));
	   * </pre>
	   * 
	   * @param items
	   *     the items that must equal the items provided by an examined {@link Collection} in any order
	   */
	@Factory
	public static <T> Matcher<Collection<? extends T>> isCollectionContainingInAnyOrder(final Collection<? extends T> items)
	{
		return collectionContainingInAnyOrder(items);
	}
	
	/**
	   * Matches a collection which contains the given items, and only the given items (i.e., if the collection 
	   * is missing any items, or if it contains any extra items, the match will fail). The items may exist 
	   * in the collection in any order.
	   * 
	   * <hr>
	   * 
	   * Creates an order agnostic matcher for {@link Collection}s that matches when a single pass over
	   * the examined {@link Collection} yields a series of items, each logically equal to one item
	   * anywhere in the specified items. For a positive match, the examined iterable
	   * must be of the same length as the number of specified items.
	   * <p/>
	   * N.B. each of the specified items will only be used once during a given examination, so be
	   * careful when specifying items that may be equal to more than one entry in an examined
	   * iterable.
	   * <p/>
	   * For example:
	   * <pre>
	   * Set&lt;String&gt; items = new HashSet<String>(Arrays.asList("bar", "foo"));
	   * assertThat(Arrays.asList("foo", "bar"), isCollectionContainingInAnyOrder(items));
	   * </pre>
	   * 
	   * @param items
	   *     the items that must equal the items provided by an examined {@link Collection} in any order
	   */
	@Factory
	@SafeVarargs
	public static <T> Matcher<Collection<? extends T>> isCollectionContainingInAnyOrder(final Matcher<? super T>... itemMatchers)
	{
		final List<Matcher<? super T>> itemMatcherList = Arrays.asList(itemMatchers);
		return collectionContainingMatchersInAnyOrder(itemMatcherList);
	}
}
