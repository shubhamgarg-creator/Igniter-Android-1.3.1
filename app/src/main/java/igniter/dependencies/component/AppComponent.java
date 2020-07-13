package igniter.dependencies.component;
/**
 * @package com.trioangle.igniter
 * @subpackage dependencies.component
 * @category AppComponent
 * @author Trioangle Product Team
 * @version 1.0
 **/

import javax.inject.Singleton;

import dagger.Component;
import igniter.adapters.chat.ChatConversationListAdapter;
import igniter.adapters.chat.MessageUserListAdapter;
import igniter.adapters.chat.NewMatchesListAdapter;
import igniter.adapters.chat.UnmatchReasonListAdapter;
import igniter.adapters.chat.UserListAdapter;
import igniter.adapters.main.ProfileSliderAdapter;
import igniter.adapters.matches.MatchesSwipeAdapter;
import igniter.adapters.profile.EditProfileImageListAdapter;
import igniter.adapters.profile.EnlargeSliderAdapter;
import igniter.adapters.profile.IgniterSliderAdapter;
import igniter.adapters.profile.LocationListAdapter;
import igniter.backgroundtask.ImageCompressAsyncTask;
import igniter.configs.RunTimePermission;
import igniter.configs.SessionManager;
import igniter.dependencies.module.AppContainerModule;
import igniter.dependencies.module.ApplicationModule;
import igniter.dependencies.module.NetworkModule;
import igniter.layoutmanager.SwipeableTouchHelperCallback;
import igniter.likedusers.LikedUserAdapter;
import igniter.likedusers.LikedUsersActivity;
import igniter.pushnotification.MyFirebaseInstanceIDService;
import igniter.pushnotification.MyFirebaseMessagingService;
import igniter.pushnotification.NotificationUtils;
import igniter.swipedeck.Utility.SwipeListener;
import igniter.utils.CommonMethods;
import igniter.utils.DateTimeUtility;
import igniter.utils.ImageUtils;
import igniter.utils.RequestCallback;
import igniter.utils.WebServiceUtils;
import igniter.views.chat.ChatConversationActivity;
import igniter.views.chat.ChatFragment;
import igniter.views.chat.CreateGroupActivity;
import igniter.views.chat.MatchUsersActivity;
import igniter.views.main.BoostDialogActivity;
import igniter.views.main.HomeActivity;
import igniter.views.main.IgniterGoldActivity;
import igniter.views.main.IgniterPageFragment;
import igniter.views.main.IgniterPlusDialogActivity;
import igniter.views.main.IgniterPlusSliderFragment;
import igniter.views.main.LoginActivity;
import igniter.views.main.SplashActivity;
import igniter.views.main.TutorialFragment;
import igniter.views.main.UserNameActivity;
import igniter.views.main.VerificationActivity;
import igniter.views.main.AccountKit.FacebookAccountKitActivity;
import igniter.views.main.AccountKit.TwilioAccountKitActivity;
import igniter.views.profile.AddLocationActivity;
import igniter.views.profile.EditProfileActivity;
import igniter.views.profile.EnlargeProfileActivity;
import igniter.views.profile.GetIgniterPlusActivity;
import igniter.views.profile.ProfileFragment;
import igniter.views.profile.SettingsActivity;
import igniter.views.signup.BirthdayFragment;
import igniter.views.signup.EmailFragment;
import igniter.views.signup.GenderFragment;
import igniter.views.signup.OneTimePwdFragment;
import igniter.views.signup.PasswordFragment;
import igniter.views.signup.PhoneNumberFragment;
import igniter.views.signup.ProfilePickFragment;
import igniter.views.signup.SignUpActivity;

/*****************************************************************
 App Component
 ****************************************************************/
@Singleton
@Component(modules = {NetworkModule.class, ApplicationModule.class, AppContainerModule.class})
public interface AppComponent {
    // ACTIVITY

    void inject(SplashActivity splashActivity);

    void inject(HomeActivity homeActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(EditProfileActivity editProfileActivity);

    void inject(GetIgniterPlusActivity getIgniterPlusActivity);

    void inject(SignUpActivity signUpActivity);

    void inject(EnlargeProfileActivity enlargeProfileActivity);

    void inject(MatchUsersActivity matchUsersActivity);

    void inject(ChatConversationActivity chatConversationActivity);

    void inject(CreateGroupActivity createGroupActivity);

    void inject(LoginActivity loginActivity);

    void inject(VerificationActivity verificationActivity);

    void inject(UserNameActivity userNameActivity);

    void inject(AddLocationActivity addLocationActivity);

    void inject(FacebookAccountKitActivity facebookAccountKitActivity);

    void inject(TwilioAccountKitActivity facebookAccountKitActivity1);

    void inject(IgniterGoldActivity igniterGoldActivity);

    void inject(LikedUsersActivity likedUsersActivity);


    // Fragments
    void inject(ProfileFragment profileFragment);

    void inject(IgniterPageFragment igniterPageFragment);

    void inject(ChatFragment chatFragment);

    void inject(ProfilePickFragment profilePickFragment);

    void inject(EmailFragment emailFragment);

    void inject(PasswordFragment passwordFragment);

    void inject(BirthdayFragment birthdayFragment);

    void inject(TutorialFragment tutorialFragment);

    void inject(PhoneNumberFragment phoneNumberFragment);

    void inject(OneTimePwdFragment oneTimePwdFragment);

    void inject(GenderFragment genderFragment);

    void inject(IgniterPlusDialogActivity igniterPlusDialogActivity);

    void inject(BoostDialogActivity boostDialogActivity);

    void inject(IgniterPlusSliderFragment igniterPlusSliderFragment);

    // Utilities
    void inject(RunTimePermission runTimePermission);

    void inject(SessionManager sessionManager);

    void inject(ImageUtils imageUtils);

    void inject(CommonMethods commonMethods);

    void inject(ProfileSliderAdapter profileSliderAdapter);

    void inject(RequestCallback requestCallback);

    void inject(DateTimeUtility dateTimeUtility);

    void inject(WebServiceUtils webServiceUtils);

    // Adapters
    void inject(IgniterSliderAdapter igniterSliderAdapter);

    void inject(NewMatchesListAdapter newMatchesListAdapter);

    void inject(MessageUserListAdapter messageUserListAdapter);

    void inject(EnlargeSliderAdapter enlargeSliderAdapter);

    void inject(EditProfileImageListAdapter editProfileImageListAdapter);

    void inject(ChatConversationListAdapter chatConversationListAdapter);

    void inject(UnmatchReasonListAdapter unmatchReasonListAdapter);

    void inject(UserListAdapter chatUserListAdapter);

    void inject(MatchesSwipeAdapter matchesSwipeAdapter);

    void inject(SwipeListener swipeListener);

    void inject(LocationListAdapter locationListAdapter);

    void inject(LikedUserAdapter likedUserAdapter);

    void inject(MyFirebaseMessagingService myFirebaseMessagingService);

    void inject(MyFirebaseInstanceIDService myFirebaseInstanceIDService);


    // AsyncTask
    void inject(ImageCompressAsyncTask imageCompressAsyncTask);

    void inject(NotificationUtils notificationUtils);

    void inject(SwipeableTouchHelperCallback swipeableTouchHelperCallback);


}
