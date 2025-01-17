package com.dvc.mybilibili.app.retrofit2.converterfactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.dvc.mybilibili.app.retrofit2.responseconverter.PegasusFeedResponseConverter;
import com.dvc.mybilibili.mvp.model.api.response.GeneralResponse;
import com.dvc.mybilibili.mvp.model.api.service.pegasus.entity.modelv2.PegasusFeedResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class FastJsonConverterFactory extends Converter.Factory {
    private int featureValues = JSON.DEFAULT_PARSER_FEATURE | Feature.DisableSpecialKeyDetect.mask;
    private Feature[] features;

    private SerializeConfig serializeConfig;
    private SerializerFeature[] serializerFeatures =
            {SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteClassName,
//            SerializerFeature.SkipTransientField,
//            SerializerFeature.DisableCircularReferenceDetect
            };

    public static FastJsonConverterFactory create() {
        return new FastJsonConverterFactory();
    }

    /**
     * 需要重写父类中responseBodyConverter，该方法用来转换服务器返回数据
     */
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new FastJsonResponseBodyConverter<>(type, ParserConfig.getGlobalInstance(), featureValues, features);
    }

    /**
     * 需要重写父类中responseBodyConverter，该方法用来转换发送给服务器的数据
     */
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new FastJsonRequestBodyConverter<>(serializeConfig, serializerFeatures);
    }

    private static class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private static final Feature[] EMPTY_SERIALIZER_FEATURES = new Feature[0];
        private Gson gson;

        private final Type type;
        private ParserConfig config;
        private int featureValues;
        private Feature[] features;

        public FastJsonResponseBodyConverter(Type type) {
            this.type = type;
        }

        public FastJsonResponseBodyConverter(Type type, ParserConfig config, int featureValues,
                                             Feature... features) {
            this.type = type;
            this.config = config;
            this.featureValues = featureValues;
            this.features = features;
            this.gson = new GsonBuilder().create();
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            if(this.type.toString().equals("byte[]")) {
                return (T) value.bytes();
            }
            if (this.type == String.class) {
                return (T) value.string();
            }
            String content = value.string();
            if(((ParameterizedType)this.type).getRawType() == GeneralResponse.class) {
                Type[] types = ((ParameterizedType)this.type).getActualTypeArguments();
                if(types != null && types.length >= 1) {
                    if(types[0] == PegasusFeedResponse.class) {
                        return (T) PegasusFeedResponseConverter.convert(
                                JSON.parseObject(content,
                                        new ParameterizedTypeImpl(new Class[]{JSONObject.class}, null, GeneralResponse.class),
                                config, featureValues, features != null ? features : EMPTY_SERIALIZER_FEATURES));
                    }
                }
            }
            try {
                return JSON.parseObject(content, type, config, featureValues,
                        features != null ? features : EMPTY_SERIALIZER_FEATURES);
            }catch (Exception e) {
                e.printStackTrace();
                return this.gson.fromJson(content, type);
            }
        }
    }

    private static class FastJsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

        private static final MediaType MEDIA_TYPE =
                MediaType.parse("application/json; charset=UTF-8");
        private final SerializeConfig serializeConfig;
        private final SerializerFeature[] serializerFeatures;

        public FastJsonRequestBodyConverter(SerializeConfig config, SerializerFeature... features) {
            this.serializeConfig = config;
            this.serializerFeatures = features;
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            byte[] content;
            if (serializeConfig != null) {
                if (serializerFeatures != null) {
                    content = JSON.toJSONBytes(value, serializeConfig, serializerFeatures);
                } else {
                    content = JSON.toJSONBytes(value, serializeConfig);
                }
            } else {
                if (serializerFeatures != null) {
                    content = JSON.toJSONBytes(value, serializerFeatures);
                } else {
                    content = JSON.toJSONBytes(value);
                }
            }
            return RequestBody.create(MEDIA_TYPE, content);
        }

    }
}
