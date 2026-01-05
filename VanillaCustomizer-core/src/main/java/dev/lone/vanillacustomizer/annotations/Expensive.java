package dev.lone.vanillacustomizer.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Expensive
{
    boolean singleCall() default true;

    boolean calledInLoop() default true;

    boolean calledInLambda() default true;
}