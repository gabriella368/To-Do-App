package com.fmi.to_do_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private void showData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String currentUserId = currentUser.getUid();

        query = firestore.collection("task").orderBy("time", Query.Direction.DESCENDING);

        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String id = documentChange.getDocument().getId();
                        ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);
                        mList.add(toDoModel);
                        adapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
                //CHANGE HERE: Сортиране на списъка по приоритет
                Collections.sort(mList, new Comparator<ToDoModel>() {
                    @Override
                    public int compare(ToDoModel o1, ToDoModel o2) {
                        // Приоритетите са във възходящ ред: "Low priority", "Medium priority", "High priority"
                        return getPriorityValue(o1.getPriority()) - getPriorityValue(o2.getPriority());
                    }

                    private int getPriorityValue(String priority) {
                        if(priority == null){
                            return 0;
                        }
                        switch (priority) {
                            case "Low priority":
                                return 3;
                            case "Medium priority":
                                return 2;
                            case "High priority":
                                return 1;
                            default:
                                return 0;
                        }
                    }
                });
                adapter.notifyDataSetChanged();
                //END OF CHANGE---

            }
        });
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            currentUserId = currentUser != null ? currentUser.getUid() : null;
            if (textView != null) {
                textView.setText(user.getEmail());
                textView.setText(currentUserId);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();

            }


        });

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();

            }
        });*/
    }
}
