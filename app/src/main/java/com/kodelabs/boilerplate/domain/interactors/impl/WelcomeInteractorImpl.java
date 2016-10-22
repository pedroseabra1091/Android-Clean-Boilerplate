package com.kodelabs.boilerplate.domain.interactors.impl;

import com.kodelabs.boilerplate.domain.executor.Executor;
import com.kodelabs.boilerplate.domain.executor.MainThread;
import com.kodelabs.boilerplate.domain.interactors.base.AbstractInteractor;
import com.kodelabs.boilerplate.domain.interactors.WelcomingInteractor;
import com.kodelabs.boilerplate.domain.repository.MessageRepository;

/**
 * Created by ricar on 22/10/2016.
 */

public class WelcomeInteractorImpl extends AbstractInteractor implements WelcomingInteractor {

    private WelcomingInteractor.Callback mCallback;
    private MessageRepository mMessageRepository;

    public WelcomeInteractorImpl(
            Executor threadExecutor,
            MainThread mainThread,
            Callback callback,
            MessageRepository messageRepository) {
        super(threadExecutor, mainThread);
        this.mCallback = callback;
        this.mMessageRepository = messageRepository;
    }

    private void notifyError() {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onRetrievalFailed("Nothing to welcome you with :(");
            }
        });
    }

    private void postMessage(final String msg) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onMessageRetrieved(msg);
            }
        });
    }

    @Override
    public void run() {
        // retrieve the message
        final String message = mMessageRepository.getWelcomeMessage();

        // check if we have failed to retrieve our message
        if(message == null || message.length() == 0) {
            // notify the failure on the main thread
            notifyError();
            return;
        }

        // we have retrieved our message, notify the UI on the main thread
        postMessage(message);
    }
}
