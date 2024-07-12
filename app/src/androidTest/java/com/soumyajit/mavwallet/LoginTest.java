package com.soumyajit.mavwallet;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.espresso.intent.Intents;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static java.util.regex.Pattern.matches;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityScenarioRule<Login> activityRule = new ActivityScenarioRule<>(Login.class);

    @Before
    public void setUp() {
        // Initialize Espresso Intents
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Espresso Intents
        Intents.release();
    }

    @Test
    public void testSuccessfulLogin() {
        // Enter valid credentials and click sign in button
        Espresso.onView(withId(R.id.regEditUserName))
                .perform(typeText("6824066641"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.editPassword))
                .perform(typeText("Test*123"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.signInForwardButton))
                .perform(click());
    }

    @Test
    public void testInvalidLogin() {
        // Enter invalid credentials and click sign in button
        Espresso.onView(withId(R.id.regEditUserName))
                .perform(typeText("123456789"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.editPassword))
                .perform(typeText("Test*125"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.signInForwardButton))
                .perform(click());
    }
}