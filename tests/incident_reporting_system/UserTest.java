package incident_reporting_system;

import static org.junit.Assert.*;

import org.junit.Test;

public class UserTest {
	
	private User firstUser;
	
	@Before
	public void setup() {
		firstUser = new User();
	}

	@Test
	public void testIdsAutomaticallyAssignedAsPositiveNumber() {
		assertTrue("Id should be positive", firstUser.getId() > 0);
	}
	
	@Test
	public void testFirstNameShouldOnlyAcceptCharactersAsInput() {
		try {
			firstUser.setFirstName("Sohail123@_12");
			fail("Expect an Illegal Excpetion that we canot enter other than chars");
		} catch(IllegalArgumentException e) {
			// verify if thrown
			assertEquals("Only enter alphabits", e.getMessage());
		}
		
	}

}
