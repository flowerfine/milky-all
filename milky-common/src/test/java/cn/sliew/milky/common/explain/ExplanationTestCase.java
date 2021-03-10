package cn.sliew.milky.common.explain;

import cn.sliew.milky.test.MilkyTestCase;
import org.junit.jupiter.api.Test;

public class ExplanationTestCase extends MilkyTestCase {

    @Test
    public void test() {
        Explanation search = new Explanation("search", "执行搜索功能");
        Explanation curdeRank = new Explanation("curdeRank", "执行粗排功能");
        Explanation rerank = new Explanation("rerank", "执行精排功能");
        Explanation finalRank = new Explanation("finalRank", "执行最终排功能");
        search.withDetail(curdeRank, rerank, finalRank);

        Explanation curdeRank1 = new Explanation("curdeRank1", "执行粗排功能阶段一");
        Explanation curdeRank2 = new Explanation("curdeRank2", "执行粗排功能阶段二");
        curdeRank.withDetail(curdeRank1, curdeRank2);

        System.out.println(search.toString());
    }
}
