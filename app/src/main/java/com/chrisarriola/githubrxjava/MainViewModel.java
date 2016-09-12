package com.chrisarriola.githubrxjava;

import android.databinding.ObservableField;

import com.manaschaudhari.android_mvvm.ViewModel;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.manaschaudhari.android_mvvm.FieldUtils.toObservable;

public class MainViewModel implements ViewModel {
    public final Observable<List<GithubRepoVM>> repositories;
    public final ObservableField<String> username = new ObservableField<>();
    public final PublishSubject<Void> onSearchClick = PublishSubject.create();

    public MainViewModel() {
        repositories =
                toObservable(this.username)
                .sample(onSearchClick)
                .switchMap(new Func1<String, Observable<List<GithubRepoVM>>>() {
            @Override
            public Observable<List<GithubRepoVM>> call(String username) {
                return GitHubClient.getInstance()
                        .getStarredRepos(username)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Func1<List<GitHubRepo>, List<GithubRepoVM>>() {
                            @Override
                            public List<GithubRepoVM> call(List<GitHubRepo> gitHubRepos) {
                                List<GithubRepoVM> vms = new ArrayList<>();
                                for (GitHubRepo repo : gitHubRepos) {
                                    vms.add(new GithubRepoVM(repo));
                                }
                                return vms;
                            }
                        });
            }
        });
    }
}
