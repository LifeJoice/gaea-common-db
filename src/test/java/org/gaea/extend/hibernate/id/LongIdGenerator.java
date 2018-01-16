package org.gaea.extend.hibernate.id;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by iverson on 2018/1/12.
 */
public class LongIdGenerator implements Runnable {
    private GaeaDateTimeLongIDGenerator idGenerator;
    private int threadLoop;
    private Set idSet;
    private final int id;

    public LongIdGenerator(GaeaDateTimeLongIDGenerator idGenerator, int threadLoop, Set idSet, int threadId) {
        this.idGenerator = idGenerator;
        this.threadLoop = threadLoop;
        this.idSet = idSet;
        this.id = threadId;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < threadLoop; j++) {
            String id = String.valueOf(idGenerator.generate(null, null));
            sb.append(id + "  ");
//            System.out.println(id);
            if (idSet.contains(id)) {
                System.out.println("-------------->>> 发现重复id！！ " + id);
            }
            idSet.add(id);
            if (j % 99 == 0) {
                System.out.println(sb.toString());
                sb = new StringBuilder();
            }
        }
        System.out.println(" 线程 [" + id + "] 执行完成。当前id池中的id数为：" + idSet.size());
    }
}
