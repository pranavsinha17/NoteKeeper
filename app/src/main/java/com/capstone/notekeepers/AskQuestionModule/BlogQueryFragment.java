package com.capstone.notekeepers.AskQuestionModule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.capstone.notekeepers.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BlogQueryFragment extends Fragment implements ChildEventListener,
        QuestionAdapter.QuestionAdapterDelegate{

    /* Constants */
    public static final String TAG = "BlogQueryFragment";
    public static final String EXTRA_QUESTION_ID_KEY = "question_id_key";
    public static final String EXTRA_QUESTION_STRING = "question_string";
    public static final String EXTRA_CURRENT_USER = "current_user_email";

    private QuestionAdapter mQuestionAdapter;
    private RecyclerView mQueryRecyclerView;
    private Context mContext;
    private FragmentActivity fragmentActivity;
    private DatabaseReference mQuestionsReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FloatingActionButton mAddQuestion;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        fragmentActivity = (FragmentActivity) context;
        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_bloquery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Ask Questions");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        // Initialise the adapter
        mQuestionAdapter = new QuestionAdapter();
        // Set BlogQueryFragment(this) as QuestionAdapter's delegate
        mQuestionAdapter.setQuestionAdapterDelegate(this);
        // Initialise Views in the layout
        mQueryRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_bloquery);

        // Set the layout, animator, and adapter for RecyclerView
        mQueryRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mQueryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mQueryRecyclerView.setAdapter(mQuestionAdapter);
        // Initialise database;
        mQuestionsReference = FirebaseDatabase.getInstance().getReference("questions");
        // Setup event listener
        mQuestionsReference.addChildEventListener(this);
        mAddQuestion = view.findViewById(R.id.add_question);
        mAddQuestion.setOnClickListener(Vieww ->{
            showEditDialog();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onStop() {
        super.onStop();
        mQuestionsReference.removeEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        Question question = dataSnapshot.getValue(Question.class);
        mQuestionAdapter.addQuestion(question);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        mQuestionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    /*
     * Method from implementing QuestionAdapter.QuestionAdapterDelegate
     */
    @Override
    public void onItemClicked(int position, List<Question> questions) {

        // The current Question item
        Question questionItem = questions.get(position);
        String questionId = questionItem.getQuestionId();
        Intent intent = new Intent(mContext, SingleQuestionActivity.class);
        intent.putExtra(EXTRA_QUESTION_ID_KEY, questionId);
        intent.putExtra(EXTRA_QUESTION_STRING, questionItem.getQuestionString());
        startActivity(intent);
    }


    public void sendQuestion(String questionText) {
        if (questionText.isEmpty()) {
            Toast.makeText(mContext, "Please enter a question...", Toast.LENGTH_SHORT).show();
        } else {
            String questionID = mQuestionsReference.push().getKey();
            if(mCurrentUser!=null && questionID!=null) {
                String userId = mCurrentUser.getUid();
                String userImageUrl = mCurrentUser.getPhotoUrl().toString();
                String userName = mCurrentUser.getDisplayName();
                Question question = new Question(questionID, questionText, (long) System.currentTimeMillis(), 0,
                        userId,userImageUrl,userName);
                mQuestionsReference.child(questionID).setValue(question);
                Toast.makeText(mContext, "Question added!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(mContext, "Invalid user", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showEditDialog() {
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        AddQuestionBottomSheet addQuestionBottomSheet = new AddQuestionBottomSheet();
        addQuestionBottomSheet.show(fm,addQuestionBottomSheet.getTag());
//        AddInputDialogFragment addInputDialogFragment = AddInputDialogFragment.newInstance("Ask a question");
//        addInputDialogFragment.show(fm, TAG);
    }
}
