package com.mambo.rafiki;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.mambo.rafiki.ui.views.activities.MainActivity;

import org.junit.Rule;

public class SharedPreferenceTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);
//
//    @Before
//    public void initValidString() {
//        // Specify a valid string.
//        stringToBetyped = "Espresso";
//    }
//
//    @Test
//    public void changeText_sameActivity() {
//        // Type text and then press the button.
//        onView(withId(R.id.general_settings)).perform(click());
//
//        // Check that the text was changed.
//        onView(withId(R.id.generalSettingsFragment))
//                .check(matches(matches(withId(R.id.generalSettingsFragment))));
//    }

}
