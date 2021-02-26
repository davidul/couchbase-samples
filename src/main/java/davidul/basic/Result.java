package davidul.basic;

import com.couchbase.client.java.codec.Transcoder;
import com.couchbase.client.java.kv.GetResult;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

public class Result {

    private GetResult getResult;

    protected byte[] content;
    protected int flags;
    private long cas;
    private Optional<Instant> expiry;
    protected Transcoder transcoder;

    private Result(GetResult r){
        this.getResult = r;
    }

    private Result(byte [] content, int flags, long cas, Optional<Instant> expiry, Transcoder transcoder){
        this.content = content;
        this.flags = flags;
        this.cas = cas;
        this.expiry = expiry;
        this.transcoder = transcoder;
    }


    public static Result of(GetResult result){
        return new Result(result);
    }

    public static Result from(byte [] content, int flags, long cas, Optional<Instant> expiry, Transcoder transcoder){
        return new Result(content, flags, cas, expiry, transcoder);
    }

    public GetResult getGetResult() {
        return getResult;
    }

    public byte[] getContent() {
        return content;
    }

    public <T> T contentAs(Class<T> target){
        return transcoder.decode(target, this.content, this.flags);
    }

    public int getFlags() {
        return flags;
    }

    public long getCas() {
        return cas;
    }

    public Optional<Instant> getExpiry() {
        return expiry;
    }

    public Transcoder getTranscoder() {
        return transcoder;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        return Arrays.equals(content, result.content);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(content);
    }
}
