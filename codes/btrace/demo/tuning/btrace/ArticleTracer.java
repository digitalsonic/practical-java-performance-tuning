package demo.tuning.btrace;

import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.TLS;

@BTrace
public class ArticleTracer {
    @TLS
    private static long beginTime;

    @OnMethod(clazz = "demo.tuning.biz.ArticleServices",
            method = "getArticleAuthorFromCache")
    public static void startTraceArticleFromCache() {
        BTraceUtils.print("getArticleFromCache");
        beginTime = BTraceUtils.timeMillis();
    }

    @OnMethod(clazz = "demo.tuning.biz.ArticleServices",
            method = "getArticleAuthorFromCache",
            location = @Location(Kind.RETURN))
    public static void endTraceArticleFromCache(Integer articleId) {
        long duration = BTraceUtils.timeMillis() - beginTime;
        BTraceUtils.print("getArticleFromCache(");
        BTraceUtils.print(articleId);
        BTraceUtils.print(") : ");
        BTraceUtils.print(duration);
        BTraceUtils.print("ms");
        BTraceUtils.println();
    }
}
