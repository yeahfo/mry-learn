package io.github.yeahfo.mry.learn.core.common.utils;

import io.github.yeahfo.mry.learn.core.common.domain.indexedfield.IndexedField;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public interface MongoCriteriaUtils {
    static Criteria regexSearch( String field, String search ) {
        Criteria criteria = new Criteria( );
        if ( isBlank( search ) ) {
            return criteria;
        }

        return criteria.andOperator( Arrays.stream( CommonUtils.splitSearchBySpace( search ) ).map( s -> where( field ).regex( s ) ).toArray( Criteria[]::new ) );
    }

    static String mongoAttributeValueFieldOf( String attributeId ) {
        return "attributeValues." + attributeId;
    }

    static String mongoAnswerFieldOf( String controlId ) {
        return "answers." + controlId;
    }

    static String mongoIndexedValueFieldOf( IndexedField field ) {
        return "ivs." + field.name( );
    }

    static String mongoTextFieldOf( IndexedField field ) {
        return "ivs." + field.name( ) + ".tv";
    }

    static String mongoSortableFieldOf( IndexedField field ) {
        return "ivs." + field.name( ) + ".sv";
    }

    static String mongoReferencedFieldOf( IndexedField field ) {
        return "ivs." + field.name( ) + ".rid";
    }

}
