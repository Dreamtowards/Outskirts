package ext.etc;

import outskirts.util.CollectionUtils;

import java.util.*;

import static outskirts.util.logging.Log.LOGGER;

public class LiteExam {

    /**
     * 贪婪法的基本步骤：
     *
     * 步骤1：从某个初始解出发；
     * 步骤2：采用迭代的过程，当可以向目标前进一步时，就根据局部最优策略，得到一部分解，缩小问题规模；
     * 步骤3：将所有解综合起来。
     */

    /**
     * 事例一：找零钱问题
     *
     * 假设你开了间小店，不能电子支付，钱柜里的货币只有 25 分、10 分、5 分和 1 分四种硬币，如果你是售货员且要找给客户 41 分钱的硬币，
     * 如何安排才能找给客人的钱既正确且硬币的个数又最少？
     *
     * 这里需要明确的几个点：
     * 1.货币只有 25 分、10 分、5 分和 1 分四种硬币；
     * 2.找给客户 41 分钱的硬币；
     * 3.硬币最少化
     */
    @SuppressWarnings("all")
    public void exa1() {
        float tSum = 41;

        float[] tvarr = {25, 10, 5, 1};  // coin types-valueSet. order by Max->Min

        List<Integer> rls = new ArrayList<>();

        while (true) {
            float tvMx = 0; // max typeValue, and <= tSum
            int _tvMx_i = -1;
            for (int i = 0;i < tvarr.length;i++) {
                if (tvarr[i] <= tSum) { tvMx = tvarr[i];_tvMx_i=i;break; }
            }
            assert tvMx > 0 && _tvMx_i!=-1;

            tSum -= tvMx;

            rls.add(_tvMx_i);

            if (tSum == 0) {
                System.out.println(rls);
                return;
            }
        }
    }

    /**
     * 事例二:背包最大价值问题
     *
     * 有一个背包，最多能承载重量为 C=150的物品，现在有7个物品（物品不能分割成任意大小），编号为 1~7，
     * 重量分别是 wi=[35,30,60,50,40,10,25]，价值分别是 pi=[10,40,30,50,35,40,30]，现在从这 7 个物品中选择一个或多个装入背包，
     * 要求在物品总重量不超过 C 的前提下，所装入的物品总价值最高。
     *
     * 这里需要明确的几个点：
     * 1.每个物品都有重量和价值两个属性；
     * 2.每个物品分被选中和不被选中两个状态（后面还有个问题，待讨论）；
     * 3.可选物品列表已知，背包总的承重量一定。
     */
    @SuppressWarnings("all")
    public void tst2() {
        int N = 7;
        float[] wi = {35,30,60,50,40,10,25};
        float[] pi = {10,40,30,50,35,40,30};
        assert wi.length==N && pi.length==N;

        float C = 150;

        class Itm { float rt; int i; }

        Itm[] itmarr = new Itm[N];
        for (int i = 0;i < N;i++) {
            itmarr[i] = new Itm();
            itmarr[i].rt = pi[i]/wi[i];
            itmarr[i].i = i;
        }
        Arrays.sort(itmarr, Comparator.comparing(itm -> itm.rt));
        CollectionUtils.reverse(itmarr);

        for (int i = 0;i < N;i++) {
            Itm itm = itmarr[i];
            if (wi[itm.i] <= C) {
                C -= wi[itm.i];
                LOGGER.info("p/ i:{}, wi:{}, pi:{}", itm.i, wi[itm.i], pi[itm.i]); // 5,1,6,3,0 / SUM(w)=150 / SUM(p)=170
            }
        }
    }

}



















