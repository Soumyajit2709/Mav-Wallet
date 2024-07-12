package com.soumyajit.mavwallet;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SignUpTest {

    private ActivityScenario<SignUp> activityScenario;

    @Before
    public void setUp() {
        // Launch SignUp activity before each test
        activityScenario = ActivityScenario.launch(SignUp.class);
    }

    @Test
    public void signUpWithValidInputs() {
        // Enter valid inputs in EditText fields
        Espresso.onView(withId(R.id.regEditUserName)).perform(ViewActions.typeText("John"));
        Espresso.onView(withId(R.id.regEditLastName)).perform(ViewActions.typeText("Doe"));
        Espresso.onView(withId(R.id.regEditPassword)).perform(ViewActions.typeText("Test1234"));
        Espresso.onView(withId(R.id.regEditConPassword)).perform(ViewActions.typeText("Test1234"));
        Espresso.onView(withId(R.id.editPhone)).perform(ViewActions.typeText("1234567890"));

        // Perform click on the registration forward button
        Espresso.onView(withId(R.id.regForwardButton)).perform(ViewActions.click());
    }

    @Test
    public void signUpWithInvalidInputs() {
        // Enter invalid inputs (empty first name)
        Espresso.onView(withId(R.id.regEditUserName)).perform(ViewActions.typeText(""));
        Espresso.onView(withId(R.id.regEditLastName)).perform(ViewActions.typeText("Doe"));
        Espresso.onView(withId(R.id.regEditPassword)).perform(ViewActions.typeText("Test1234"));
        Espresso.onView(withId(R.id.regEditConPassword)).perform(ViewActions.typeText("Test1234"));
        Espresso.onView(withId(R.id.editPhone)).perform(ViewActions.typeText("1234567890"));

        // Perform click on the registration forward button
        Espresso.onView(withId(R.id.regForwardButton)).perform(ViewActions.click());
    }
}
