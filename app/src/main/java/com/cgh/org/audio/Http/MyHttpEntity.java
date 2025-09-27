package com.cgh.org.audio.Http;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 自定義 HTTP 實體 - 用於 OkHttp
 */
public class MyHttpEntity extends RequestBody {
    
    private final RequestBody delegate;
    private final ProgressListener progressListener;
    
    public interface ProgressListener {
        void onProgress(long bytesWritten, long contentLength);
    }
    
    public MyHttpEntity(RequestBody delegate, ProgressListener progressListener) {
        this.delegate = delegate;
        this.progressListener = progressListener;
    }
    
    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }
    
    @Override
    public long contentLength() throws IOException {
        return delegate.contentLength();
    }
    
    @Override
    public void writeTo(okio.BufferedSink sink) throws IOException {
        if (progressListener != null) {
            ProgressOutputStream progressSink = new ProgressOutputStream(sink, progressListener, contentLength());
            delegate.writeTo(progressSink);
        } else {
            delegate.writeTo(sink);
        }
    }
    
    private static class ProgressOutputStream extends okio.ForwardingSink {
        private final ProgressListener progressListener;
        private final long contentLength;
        private long bytesWritten = 0;
        
        public ProgressOutputStream(okio.Sink delegate, ProgressListener progressListener, long contentLength) {
            super(delegate);
            this.progressListener = progressListener;
            this.contentLength = contentLength;
        }
        
        @Override
        public void write(okio.Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            if (progressListener != null) {
                progressListener.onProgress(bytesWritten, contentLength);
            }
        }
    }
}