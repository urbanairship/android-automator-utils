/*
 * Copyright 2013 Urban Airship
 */

package com.urbanairship.automatorutils;

import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;

/**
 * Helper class to test the preferences
 *
 */
public class PreferencesHelper {

    private static int UI_OBJECTS_WAIT_TIME = 1000;  // 1 second
    private static int MAX_REGISTRATION_WAIT_TIME = 180000;  // 3 minutes in case of long registration
    private static int KEYBOARD_WAIT_TIME = 3000;  // 3 seconds
    private static int SET_ALIAS_TEXT_WAIT_TIME = 3000;  // 3 seconds

    private UiSelector getPreferenceSummarySelector(String description) {
        return new UiSelector().description(description)
                .childSelector(new UiSelector()
                .className("android.widget.RelativeLayout")
                .childSelector(new UiSelector().index(1)));
    }

    private UiSelector getPreferenceTitleSelector(String description) {
        return new UiSelector().description(description)
                .childSelector(new UiSelector()
                .className("android.widget.RelativeLayout")
                .childSelector(new UiSelector().index(0)));
    }

    /**
     * Check specified preference view is enabled
     * @param setting The specified preference setting
     * @throws UiObjectNotFoundException
     * @throws InterruptedException
     */
    public boolean isPreferenceViewEnabled(String setting) throws UiObjectNotFoundException, InterruptedException {
        UiObject preferenceView = new UiObject(new UiSelector().description(setting));
        scrollPreferenceIntoView(setting);
        return preferenceView.isEnabled();
    }

    /**
     * Set the specified preference setting
     * @param setting The specified preference to be set
     * @param enabled Boolean to enable or disable the specified setting
     * @throws Exception
     */
    public void setPreferenceCheckBoxEnabled(String setting, boolean enabled) throws Exception {
        // Scroll to the preference if its not visible in the list
        scrollPreferenceIntoView(setting);

        UiObject preference = new UiObject(new UiSelector().description(setting));
        AutomatorUtils.waitForUiObjectsToExist(UI_OBJECTS_WAIT_TIME, preference);
        UiObject preferenceCheckBox =  preference.getChild(new UiSelector().className(android.widget.CheckBox.class));
        AutomatorUtils.waitForUiObjectsToExist(UI_OBJECTS_WAIT_TIME, preferenceCheckBox);
        if (preferenceCheckBox.isChecked() != enabled) {
            preferenceCheckBox.click();
        }
    }

    /**
     * Toggle the checkbox for specified preference setting
     * @param setting The specified preference to select
     * @return <code>true</code> if checkbox is selected, otherwise <code>false</code>
     * @throws UiObjectNotFoundException
     * @throws InterruptedException
     */
    public boolean getCheckBoxSetting(String setting) throws UiObjectNotFoundException, InterruptedException {
        scrollPreferenceIntoView(setting);

        UiObject settingCheckBox = new UiObject(new UiSelector().description(setting));

        settingCheckBox.click();
        return settingCheckBox.isChecked();
    }

    /**
     * Change the time preference value
     * @param setting The specified preference to change
     * @throws UiObjectNotFoundException
     * @throws InterruptedException
     */
    public void changeTimePreferenceValue(String setting) throws UiObjectNotFoundException, InterruptedException {
        // Scroll to the preference if its not visible in the list
        scrollPreferenceIntoView(setting);

        UiObject timePicker = new UiObject(new UiSelector().description(setting));
        UiObject okButton = new UiObject(new UiSelector().className("android.widget.Button").text("OK"));

        timePicker.click();

        // Change the time
        for (int i = 0; i < 3; i++) {
            UiObject numberPicker = new UiObject(new UiSelector().className("android.widget.NumberPicker").index(i));
            UiObject button = numberPicker.getChild(new UiSelector().className("android.widget.Button"));
            button.click();
        }

        okButton.click();
    }

    /**
     * Get the preference summary
     * @param setting The specified preference
     * @return The string value of the preference
     * @throws UiObjectNotFoundException
     * @throws InterruptedException
     */
    public String getPreferenceSummary(String setting) throws UiObjectNotFoundException, InterruptedException {
        String summaryString = "";
        // Scroll to the preference if its not visible in the list
        UiScrollable listView = new UiScrollable(new UiSelector().className("android.widget.ListView"));
        UiSelector summary = this.getPreferenceSummarySelector(setting);
        listView.scrollIntoView(summary);
        UiObject summaryText = new UiObject(summary);
        AutomatorUtils.waitForUiObjectsToExist(MAX_REGISTRATION_WAIT_TIME, summaryText);
        if (summaryText.exists()) {
            summaryString = summaryText.getText();
        }
        return summaryString;
    }

    /**
     * Set an alias
     * @param alias The string to set to
     * @throws UiObjectNotFoundException
     * @throws InterruptedException
     */
    public void setAlias(String alias) throws UiObjectNotFoundException, InterruptedException {
        // Test set alias
        // Scroll to the preference if its not visible in the list
        scrollPreferenceIntoView("SET_ALIAS");

        UiObject setAlias = new UiObject(new UiSelector().description("SET_ALIAS"));
        UiObject aliasStringDisplayed = new UiObject(new UiSelector().text(alias));
        boolean aliasExist = false;
        if (aliasStringDisplayed.exists()) {
            aliasExist = true;
        }

        setAlias.click();
        UiObject aliasEditText = new UiObject(new UiSelector().text(alias));
        AutomatorUtils.waitForUiObjectsToExist(UI_OBJECTS_WAIT_TIME, aliasEditText);

        // Check if an alias already exist
        if (aliasExist) {
            aliasEditText.click();
            UiObject deleteAlias = new UiObject(new UiSelector().text("Delete"));
            if (deleteAlias.exists()) {
                // Alias exist, so clear it
                deleteAlias.click();
                UiObject okButton = new UiObject(new UiSelector().text("OK"));
                okButton.click();
                setAlias.click();
            }
        }

        UiObject setAliasText = new UiObject(new UiSelector().className("android.widget.EditText"));
        AutomatorUtils.waitForUiObjectsToExist(UI_OBJECTS_WAIT_TIME, setAliasText);
        setAliasText.click();

        // Wait for keyboard to pop up
        Thread.sleep(KEYBOARD_WAIT_TIME);

        // Set the alias
        AutomatorUtils.waitForUiObjectsToExist(SET_ALIAS_TEXT_WAIT_TIME, setAliasText);
        setAliasText.setText(alias);

        // save
        UiObject okButton = new UiObject(new UiSelector().text("OK"));
        okButton.click();
    }

    /**
     * Set a tag
     * @param tags The string to set to
     * @throws UiObjectNotFoundException
     * @throws InterruptedException
     */
    public void setTags(String tags) throws UiObjectNotFoundException, InterruptedException {
        // Scroll to the preference if its not visible in the list
        scrollPreferenceIntoView("SET_TAGS");

        UiObject okButton = new UiObject(new UiSelector().text("OK"));
        UiObject setTags = new UiObject(new UiSelector().description("SET_TAGS"));
        setTags.click();

        // Check if a tag already exist
        UiObject tagsListView = new UiObject(new UiSelector().className("android.widget.ListView"));
        AutomatorUtils.waitForUiObjectsToExist(UI_OBJECTS_WAIT_TIME, tagsListView);

        if (tagsListView.exists()) {
            UiObject tagLinearLayout = tagsListView.getChild(new UiSelector().className("android.widget.LinearLayout"));
            UiObject tagDeleteButton = tagLinearLayout.getChild(new UiSelector().className("android.widget.ImageButton"));
            tagDeleteButton.click();
            okButton = new UiObject(new UiSelector().text("OK"));
            okButton.click();
            setTags.click();
        }

        // Set tag
        UiObject setTagsText = new UiObject(new UiSelector().className("android.widget.EditText"));

        // Add first tag
        setTagsText.click();

        // Wait for keyboard to pop up
        Thread.sleep(KEYBOARD_WAIT_TIME);

        setTagsText.setText(tags);
        UiObject addTagButton = new UiObject(new UiSelector().className("android.widget.ImageButton"));
        addTagButton.click();

        // Save first tag
        okButton = new UiObject(new UiSelector().text("OK"));
        okButton.click();
    }

    /**
     * Scrolls to the preference setting's title in the UI view
     * @param setting The specified preference setting
     * @throws UiObjectNotFoundException
     * @throws InterruptedException
     */
    private void scrollPreferenceIntoView(String setting) throws UiObjectNotFoundException, InterruptedException {
        UiScrollable listView = new UiScrollable(new UiSelector().className("android.widget.ListView"));
        AutomatorUtils.waitForUiObjectsToExist(UI_OBJECTS_WAIT_TIME, listView);
        listView.scrollIntoView(getPreferenceTitleSelector(setting));
    }
}
