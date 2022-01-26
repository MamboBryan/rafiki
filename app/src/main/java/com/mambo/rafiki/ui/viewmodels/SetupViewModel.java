package com.mambo.rafiki.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mambo.rafiki.data.entities.Response;
import com.mambo.rafiki.data.entities.User;
import com.mambo.rafiki.repositories.UserRepository;

public class SetupViewModel extends AndroidViewModel {

    private UserRepository repository;

    public LiveData<Response> response;
    private User user;

    public SetupViewModel(@NonNull Application application) {
        super(application);

        repository = UserRepository.getInstance(application);
        response = repository.getResponse();

        user = new User();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateUserToken(String token) {
        user.setToken(token);
    }

    public void updateUser() {
        response = repository.updateUser(user);
    }
}
