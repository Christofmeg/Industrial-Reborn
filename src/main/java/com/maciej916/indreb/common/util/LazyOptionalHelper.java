package com.maciej916.indreb.common.util;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LazyOptionalHelper<T> {

    private final LazyOptional<T> lazyOptional;

    //TODO: Replace this with a static method
    public LazyOptionalHelper(@Nonnull LazyOptional<T> lazyOptional) {
        this.lazyOptional = lazyOptional;
    }

    public boolean isPresent() {
        return lazyOptional.isPresent();
    }

    /**
     * @return The value of the optional or null if there is none.
     */
    @Nullable
    public T getValue() {
        if (lazyOptional.isPresent()) {
            return lazyOptional.orElseThrow(() -> new RuntimeException("Failed to retrieve value of lazy optional when it said it was present"));
        }
        return null;
    }

    public void ifPresent(@Nonnull NonNullConsumer<? super T> consumer) {
        lazyOptional.ifPresent(consumer);
    }

    public void ifPresentElse(NonNullConsumer<? super T> presentConsumer, Runnable elseConsumer) {
        if (isPresent()) {
            lazyOptional.ifPresent(presentConsumer);
        } else {
            elseConsumer.run();
        }
    }

    @Nullable
    public <RESULT> RESULT getIfPresent(Function<? super T, RESULT> function) {
        if (isPresent()) {
            return function.apply(getValue());
        }
        return null;
    }

    //For when the result is not a constant so we don't want to evaluate it if our LazyOptional is present
    public <RESULT> RESULT getIfPresentElseDo(Function<? super T, RESULT> presentFunction, Supplier<RESULT> elseResult) {
        if (isPresent()) {
            return presentFunction.apply(getValue());
        }
        return elseResult.get();
    }

    //TODO: Do we want to add a getIfMatchesElse?
    public <RESULT> RESULT getIfPresentElse(Function<? super T, RESULT> presentFunction, RESULT elseResult) {
        if (isPresent()) {
            return presentFunction.apply(getValue());
        }
        return elseResult;
    }

    public boolean matches(Predicate<? super T> predicate) {
        if (isPresent()) {
            return predicate.test(getValue());
        }
        return false;
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(getValue());
    }

    /**
     * Static version of toOptional to bypass even creating a LazyOptionalHelper object
     */
    public static <T> Optional<T> toOptional(LazyOptional<T> lazyOptional) {
        //TODO: Transition some of the usages of LazyOptionalHelper to just using this method?
        if (lazyOptional.isPresent()) {
            return Optional.of(lazyOptional.orElseThrow(() -> new RuntimeException("Failed to retrieve value of lazy optional when it said it was present")));
        }
        return Optional.empty();
    }
}