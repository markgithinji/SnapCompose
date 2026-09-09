# Testing Strategy Refinement

This plan aims to fully adopt "Fake-based" testing across all modules, centralize test utilities into `:core:testing`, and eliminate the remaining Mockito dependencies in unit tests.

## Proposed Changes

### [core:testing]
- **[NEW] [FakeAppDatabase](file:///C:/Users/Mark/AndroidStudioProjects/PublicRepo/ComposeGallery/core/testing/src/main/java/com/example/composegallery/core/testing/FakeAppDatabase.kt)**: A fake implementation of the Room database to avoid using `mock()` for database components in repository tests.
- **[MOVE] [MainDispatcherRule](file:///C:/Users/Mark/AndroidStudioProjects/PublicRepo/ComposeGallery/core/testing/src/main/java/com/example/composegallery/core/testing/util/MainDispatcherRule.kt)**: Centralize the coroutine dispatcher rule used by all ViewModels.
- **[MOVE] [PagingTestUtils](file:///C:/Users/Mark/AndroidStudioProjects/PublicRepo/ComposeGallery/core/testing/src/main/java/com/example/composegallery/core/testing/util/PagingTestUtils.kt)**: Centralize `collectItemsForTest` to avoid duplication.

### [core:network]
- **[MODIFY] [BaseUnsplashPagingSourceTest](file:///C:/Users/Mark/AndroidStudioProjects/PublicRepo/ComposeGallery/core/network/src/test/java/com/example/composegallery/core/network/paging/BaseUnsplashPagingSourceTest.kt)**: Replace `mock<StringProvider>()` with `FakeStringProvider`.
- **[DELETE] [collectItemsForTest](file:///C:/Users/Mark/AndroidStudioProjects/PublicRepo/ComposeGallery/core/network/src/test/java/com/example/composegallery/core/util/collectItemsForTest.kt)**: Use the centralized version.

### [feature:home]
- **[MODIFY] [DefaultGalleryRepositoryTest](file:///C:/Users/Mark/AndroidStudioProjects/PublicRepo/ComposeGallery/feature/home/src/test/java/com/example/composegallery/feature/home/data/repository/DefaultGalleryRepositoryTest.kt)**: Replace `mock<AppDatabase>()` with `FakeAppDatabase`.
- **[MODIFY] [GalleryViewModelTest](file:///C:/Users/Mark/AndroidStudioProjects/PublicRepo/ComposeGallery/feature/home/src/test/java/com/example/composegallery/feature/home/ui/GalleryViewModelTest.kt)**: Clean up unused Mockito imports.

### [feature:search]
- **[DELETE] [collectItemsForTest](file:///C:/Users/Mark/AndroidStudioProjects/PublicRepo/ComposeGallery/feature/search/src/test/java/com/example/composegallery/core/util/collectItemsForTest.kt)**: Use the centralized version.

## Verification Plan

### Automated Tests
- Run all unit tests to ensure no regressions:
  `./gradlew testDebugUnitTest`
- Specifically verify the affected test files in the Test Runner.

### Manual Verification
- Check the project structure for duplication and ensure all modules correctly depend on `:core:testing` for their test needs.
