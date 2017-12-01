package ch.voulgarakis.icsc2018.recruitment.utils;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import ch.voulgarakis.icsc2018.recruitment.model.Skill;
import ch.voulgarakis.icsc2018.recruitment.model.SkillList;

public class SkillListDeserializer extends JsonDeserializer<SkillList> {

    @Override
    public SkillList deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        return new SkillList(p.readValueAs(new TypeReference<List<Skill>>() {
        }));
    }

}
