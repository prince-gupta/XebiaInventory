package com.xebia.common;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Created by Pgupta on 24-11-2016.
 */
public final class StringUtils {

    public final static Predicate<String> isBlank = org.apache.commons.lang3.StringUtils::isBlank;
    public final static Predicate<String> isNotBlank = isBlank.negate();
    public final static BiPredicate<String, String> areBothNotBlank = (s,p) -> isNotBlank.test(s) && isNotBlank.test(p);
    public final static BiPredicate<String, String> areBothEquals = (s,p) -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(s,p);
    public final static BiPredicate<String, String> areBothNotBlankAndEqual = (s,p) -> areBothNotBlank.and(areBothEquals).test(s,p);
}
