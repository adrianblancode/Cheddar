package org.robolectric.integrationtests.sparsearray;

import android.util.SparseArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Config.ALL_SDKS)
public class SparseArraySetTest {

    @Test
    public void testSparseArrayBracketOperator_callsSetMethodPreApi31() {
        // Setup
        final SparseArray<String> sparseArray = new SparseArray<>();
        sparseArray.set(0, "Blizzard");
        sparseArray.set(1, "Blizzara");
        // Assertions
        assertThat(sparseArray.get(0)).isEqualTo("Blizzard");
        assertThat(sparseArray.get(1)).isEqualTo("Blizzara");
    }
}
