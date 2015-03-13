package org.baracus.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit Test for DataUtil
 */
public class DataUtilTest {

    /**
     * Sample class
     */
    private class FooBatz {
        Long id;
        String name;

        public FooBatz(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FooBatz fooBatz = (FooBatz) o;

            if (!id.equals(fooBatz.id)) return false;
            if (name != null ? !name.equals(fooBatz.name) : fooBatz.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }

    /**
     * Test Hashifier, hashifyById
     */
    private class TestHashifier implements DataUtil.Hashifier<Long, FooBatz> {

        @Override
        public Long getValue(FooBatz item) {
            return item != null ? item.getId() : null;
        }
    }


    /**
     * Some Probes
     */
    private final FooBatz probeHugo = new FooBatz(1L, "Victor Hugo");
    private final FooBatz probeKonsalik = new FooBatz(2L, "Konsalik");
    private final FooBatz probeShakespeare =  new FooBatz(3L, "Shakespeare");

    // for list test
    private final FooBatz probeKonsalik2 = new FooBatz(2L, "Konsalik2");


    /**
     * Positive Test, test a correct 1:1 List
     */
    @Test
    public void testHashify() throws Exception {
        List<FooBatz> fooBatzs = Arrays.asList(probeHugo,probeKonsalik, probeShakespeare);
        Map<Long, FooBatz> probes =  DataUtil.hashify(fooBatzs, new TestHashifier());
        assertNotNull(probes);
        assertEquals(3, probes.entrySet().size());
        assertEquals(probeHugo, probes.get(1L));
        assertEquals(probeKonsalik, probes.get(2L));
        assertEquals(probeShakespeare, probes.get(3L));
    }


    /**
     * Negative Test, this is not a proper 1:1 List
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testHashify2() {
        List<FooBatz> fooBatzs = Arrays.asList(probeHugo,probeKonsalik, probeKonsalik2, probeShakespeare);
        Map<Long, FooBatz> probes =  DataUtil.hashify(fooBatzs, new TestHashifier());
    }

    /**
     * Positive Test for a List
     * @throws Exception
     */
    @Test
    public void testHashify2List() throws Exception {
        List<FooBatz> fooBatzs = Arrays.asList(probeHugo, probeKonsalik, probeKonsalik2, probeShakespeare);
        Map<Long, List<FooBatz>> probe = DataUtil.hashify2List(fooBatzs, new TestHashifier());
        assertNotNull(probe);
        assertEquals(3, probe.size());
        assertEquals(1, probe.get(1L).size());
        assertEquals(2, probe.get(2L).size());
        assertEquals(1, probe.get(3L).size());
        List<FooBatz> sublist = probe.get(2L);
        assertTrue(sublist.contains(probeKonsalik));
        assertTrue(sublist.contains(probeKonsalik2));
    }
}