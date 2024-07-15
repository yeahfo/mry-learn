package io.github.yeahfo.mry.learn.core.common.domain;

import io.github.yeahfo.mry.learn.core.common.utils.Identified;
import io.github.yeahfo.mry.learn.core.common.validation.id.shoruuid.ShortUuid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static io.github.yeahfo.mry.learn.core.common.utils.MryConstants.MAX_URL_LENGTH;

@Builder
public record UploadedFile( @NotBlank
                            @ShortUuid
                            String id, // id，用于前端loop时作为key
                            @Size( max = 200 )
                            String name,//文件名称
                            @NotBlank
                            @Size( max = 500 )
                            String type,//文件类型
                            @NotBlank
                            @Size( max = MAX_URL_LENGTH )
                            String fileUrl,//文件url
                            @Size( max = 500 )
                            String ossKey,//阿里云的文件key
                            @Min( 0 )
                            int size//文件大小
) implements Identified {
    @Override
    public String identifier( ) {
        return id;
    }

    public UploadedFile( String id, String type, String fileUrl, String ossKey, int size ) {
        this( id, "未命名", type, fileUrl, ossKey, size );
    }
}
