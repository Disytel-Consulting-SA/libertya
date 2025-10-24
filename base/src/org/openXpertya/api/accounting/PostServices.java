package org.openXpertya.api.accounting;

import java.util.ServiceLoader;

public final class PostServices {
    private static volatile PostService INSTANCE;

    private PostServices() {}

    public static PostService get() {
        PostService local = INSTANCE;
        if (local == null) {
            synchronized (PostServices.class) {
                local = INSTANCE;
                if (local == null) {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl == null) cl = PostServices.class.getClassLoader();

                    ServiceLoader<PostService> loader = ServiceLoader.load(PostService.class, cl);

                    // iterar hasta el primero
                    for (PostService svc : loader) {
                        local = svc;
                        break;
                    }

                    if (local == null) {
                        throw new IllegalStateException(
                            "No hay implementación de PostService en el classpath. " +
                            "Verificá META-INF/services/org.openXpertya.api.accounting.PostService " +
                            "dentro del jar de serverRoot."
                        );
                    }
                    INSTANCE = local;
                }
            }
        }
        return local;
    }
}

