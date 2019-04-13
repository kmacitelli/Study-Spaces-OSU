package com.example.studyspacesosu;

import android.support.annotation.NonNull;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

public class RegisterPasswordShortTest {
    private FirebaseAuth mAuth;
    private ViewInteraction email;
    private ViewInteraction password;
    private View emailView;
    private FirebaseFirestore mDataBase;
    @Rule
    public ActivityTestRule<LoginActivity> activityActivityTestRule = new ActivityTestRule<LoginActivity> (LoginActivity.class);
    @Before
    public void Setup(){
        mDataBase= FirebaseFirestore.getInstance();
        email = onView(withId(R.id.email));
        password = onView(withId(R.id.password));


    }
    @Test
    public void registerPasswordIsShort(){
        ViewAction emailText= ViewActions.replaceText("ego.11@osu.edu");
        email.perform(emailText);
        ViewAction passwordText= ViewActions.replaceText("stu");
        password.perform(passwordText);
        ViewInteraction register = onView(withId(R.id.email_register_button));
        ViewAction registerClick= ViewActions.click();
        register.perform(registerClick);
        mDataBase.collection("user").whereEqualTo("username","ego.11@osu.edu" ).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
