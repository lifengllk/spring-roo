package org.springframework.roo.project.packaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.roo.model.JavaType;

/**
 * Unit test of {@link PackagingProviderConverter}
 *
 * @author Andrew Swan
 * @since 1.2.0
 */
public class PackagingProviderConverterTest {

	// Constants
	private static final String UNKNOWN = "no-such-id";
	private static final String CORE_JAR_ID = "jar";
	private static final String CUSTOM_JAR_ID = "jar_custom";
	private static final String CORE_WAR_ID = "war";

	// Fixture
	private PackagingProviderConverter converter;
	@Mock private PackagingProvider mockCoreJarPackaging;
	@Mock private PackagingProvider mockCustomJarPackaging;
	@Mock private PackagingProvider mockWarPackaging;

	@Before
	public void setUp() {
		// Mocks
		MockitoAnnotations.initMocks(this);
		when(mockCoreJarPackaging.getId()).thenReturn(CORE_JAR_ID);
		when(mockCustomJarPackaging.getId()).thenReturn(CUSTOM_JAR_ID);
		when(mockWarPackaging.getId()).thenReturn(CORE_WAR_ID);

		// Object under test
		this.converter = new PackagingProviderConverter();
		this.converter.bindPackagingType(mockCoreJarPackaging);
		this.converter.bindPackagingType(mockCustomJarPackaging);
		this.converter.bindPackagingType(mockWarPackaging);
	}
	
	@Test
	public void testDoesNotSupportWrongType() {
		assertFalse(converter.supports(JavaType.class, null));
	}

	@Test
	public void testSupportsCorrectType() {
		assertTrue(converter.supports(PackagingProvider.class, null));
	}

	/**
	 * Asserts that the given string can't be converted to a {@link PackagingProvider}
	 *
	 * @param string the string to convert (can be blank)
	 */
	private void assertInvalidString(final String string) {
		try {
			converter.convertFromText(string, PackagingProvider.class, null);
			fail("Expected a " + UnsupportedOperationException.class);
		} catch (UnsupportedOperationException expected) {
			assertEquals("Unsupported packaging type '" + string + "'", expected.getMessage());
		}
	}

	@Test
	public void testConvertNullString() {
		assertInvalidString(null);
	}

	@Test
	public void testConvertEmptyString() {
		assertInvalidString("");
	}

	@Test
	public void testConvertUnknownString() {
		assertInvalidString("ear");
	}

	@Test
	public void testConvertPartialString() {
		assertInvalidString(CORE_WAR_ID.substring(0, 1));
	}

	@Test
	public void testConvertValidString() {
		// Invoke and check
		assertEquals(mockCustomJarPackaging, converter.convertFromText(CUSTOM_JAR_ID, PackagingProvider.class, null));
	}

	@Test
	public void testGetAllPossibleValuesForInvalidExistingData() {
		assertPossibleValues(UNKNOWN);
	}

	@Test
	public void testGetAllPossibleValuesForNullExistingData() {
		assertPossibleValues(null, CORE_JAR_ID.toUpperCase(), CUSTOM_JAR_ID.toUpperCase(), CORE_WAR_ID.toUpperCase());
	}

	@Test
	public void testGetAllPossibleValuesForEmptyExistingData() {
		assertPossibleValues("", CORE_JAR_ID.toUpperCase(), CUSTOM_JAR_ID.toUpperCase(), CORE_WAR_ID.toUpperCase());
	}

	@Test
	public void testGetAllPossibleValuesForLowerCaseExistingData() {
		assertPossibleValues("j", CORE_JAR_ID.toUpperCase(), CUSTOM_JAR_ID.toUpperCase());
	}

	@Test
	public void testGetAllPossibleValuesForUpperCaseExistingData() {
		assertPossibleValues("J", CORE_JAR_ID.toUpperCase(), CUSTOM_JAR_ID.toUpperCase());
	}

	/**
	 * Asserts that the completions for the given existing data are as expected
	 *
	 * @param existingData can't be <code>null</code> (not part of the contract)
	 * @param expectedCompletions
	 */
	private void assertPossibleValues(final String existingData, final String... expectedCompletions) {
		// Set up
		final List<String> completions = new ArrayList<String>();

		// Invoke
		final boolean addSpace = converter.getAllPossibleValues(completions, PackagingProvider.class, existingData, null, null);

		// Check
		assertTrue(addSpace);
		assertEquals(Arrays.asList(expectedCompletions), completions);
	}
}