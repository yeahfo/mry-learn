package io.github.yeahfo.mry.learn.core.common.domain.indexedfield;

import lombok.Builder;

import java.util.Set;

@Builder
public record IndexedValue( String rid,//referenceId，引用ID，不能索引，因为MongoDB最大只支持64个索引
                            Double sv,//sortableValue，可排序字段，需要索引
                            Set< String > tv//textValues，可搜索字段，用于按控件统计或重复数据检查，对于有option的控件为optionIds，对于邮件控件为邮箱地址，需要索引
) {

}
