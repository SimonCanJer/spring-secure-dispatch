package com.secured.api.data;

import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @Author: Simon Cantor
 * the interface provides to subscribe for impersonation notification events:
 *
 */
public interface ImpersonationListener {
    /**
     * just registers consumer for impersonation
     * @param listener
     */
    void onImpersonated(Consumer<Serializable> listener);

    /**
     * called when an impersonated request left
     * @param listener
     */
    void onLeavingImpersonatedRequest(Consumer<Consumer<Serializable>> listener);

}
