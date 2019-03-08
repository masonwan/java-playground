package masonwan.playground;

import com.google.common.collect.Lists;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtensionMethod({
    String.class, Extensions.class
})
public class PlayLombok {
    @Test
    public void testVal_setValue() throws Exception {
        val x = 2;
        assertThat(x).isEqualTo(2);
    }

    @Test
    public void testVal_assignValue() throws Exception {
        val x = 2;
        // x = 3; // This line would not even compile.
    }

    @Test
    public void testVal_forEach() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("Name");
        list.add("Age");

        for (val value : list) {
            val trimed = value.trim();
        }
    }

    @Test
    public void testVal_ambiguousType() throws Exception {
        val isMap = false;
        // The IDE might show error because it does not support Lombok syntax yet.
        val x = isMap ? new HashMap<>() : new ArrayList<>();
        assertThat(x.getClass().getName()).isEqualTo(ArrayList.class.getName());
    }

    @Test(
        expectedExceptions = NullPointerException.class
    )
    public void testNonNull_atField() throws Exception {
        @AllArgsConstructor
        class A {
            @NonNull
            String nullNull;
        }

        new A(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNonNull_atArgument() throws Exception {
        class A {
            void doSomething(@NonNull Object notNull) {
            }
        }
        new A().doSomething(null);
    }

    @Test
    public void testCleanup() throws Exception {
        @Accessors(fluent = true)
        @Getter
        @Setter
        class A {
            FileWriter writer;
            FileReader reader;

            void readWriteFile() throws IOException {

                @Cleanup
                FileWriter writer = this.writer;

                @Cleanup
                FileReader reader = this.reader;
                int c;
                while ((c = reader.read()) >= 0) {
                    System.out.print((char) c);
                }
            }
        }

        FileWriter writer = mock(FileWriter.class);
        FileReader reader = mock(FileReader.class);
        willReturn(-1)
            .given(reader)
            .read();
        A a = new A()
            .writer(writer)
            .reader(reader);

        a.readWriteFile();

        verify(writer)
            .close();
        verify(reader)
            .close();
    }

    // @Setter // This won't work.
    enum EnumWithSetter {
        Type1,
        Type2,
    }

    @Test
    public void testBuilder() throws Exception {
        ClassWithBuilder classWithBuilder = ClassWithBuilder.builder()
            .x(2)
            .y("Hi")
            .name("1")
            .names(Lists.newArrayList("2", "3"))
            .build();

        assertThat(classWithBuilder.x())
            .isEqualTo(2);
        assertThat(classWithBuilder.y())
            .isEqualTo("Hi");
        assertThat(classWithBuilder.names())
            .containsExactly("1", "2", "3");
    }

    @Test
    public void testFieldDefaults() throws Exception {
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        class A {
            int field = 0;
        }

        A a = new A();

        Field field = a.getClass().getDeclaredField("field");
        assertThat(Modifier.isFinal(field.getModifiers()))
            .isTrue();
        assertThat(Modifier.isPrivate(field.getModifiers()))
            .isTrue();
    }

    @Test
    public void testUtilityClass() throws Exception {
        // The IDE might show error because it does not support Lombok syntax yet.
        Utility.print("Hi");
    }

    @Test
    public void testExtensionMethod() throws Exception {
        // The IDE might show error because it does not support Lombok syntax yet.
        assertThat("hi".toTitleCase()).isEqualTo("Hi");
    }
}

class Extensions {
    public static <T> T or(T obj, T ifNull) {
        return obj != null ? obj : ifNull;
    }

    public static String toTitleCase(String in) {
        if (in.isEmpty()) return in;
        return "" + Character.toTitleCase(in.charAt(0)) +
            in.substring(1).toLowerCase();
    }
}

@UtilityClass
class Utility {
    public void print(String message) {
        System.out.println(message);
    }
}

@Accessors(fluent = true)
@Getter
@Builder
class ClassWithBuilder {
    int x;
    String y;
    @Singular
    List<String> names = new ArrayList<>();
}