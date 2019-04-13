
    package com.example.studyspacesosu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.TypeTextAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.ViewInteraction;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.Toast;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.*;
    /**
     * Instrumented test, which will execute on an Android device.
     *
     * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
     */
    @RunWith(AndroidJUnit4.class)
    public class RegisterBlankEmailTest {
        private FirebaseAuth mAuth;
        private ViewInteraction email;
        private ViewInteraction password;
        private View emailView;
        private FirebaseFirestore mDataBase;
        @Rule
        public ActivityTestRule<LoginActivity> activityActivityTestRule = new ActivityTestRule<LoginActivity> (LoginActivity.class);
        @Before
        public void Setup(){
            mDataBase=FirebaseFirestore.getInstance();
            mAuth=FirebaseAuth.getInstance();
            email = onView(withId(R.id.email));
            password = onView(withId(R.id.password));

            if(mAuth.getCurrentUser()!=null){
                mAuth.signOut();
            }
        }
    @Test
    public void registerBlankEmail(){
        ViewAction emailText= ViewActions.replaceText("");
        email.perform(emailText);
        ViewAction passwordText= ViewActions.replaceText("study");
        password.perform(passwordText);
        ViewInteraction register = onView(withId(R.id.email_register_button));
        ViewAction registerClick= ViewActions.click();
        register.perform(registerClick);
        mDataBase.collection("user").whereEqualTo("username","").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    boolean present;
                    if(docs.size()>0){
                        present=true;
                    }
                    else{
                        present=false;
                    }
                    assertTrue(!present);
                }
            }
        });
    }
}
