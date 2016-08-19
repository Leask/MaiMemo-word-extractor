//
//  TextIteratorsTest.m
//  WordExtractor
//
//  Created by TJT on 8/18/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "TextIterators.h"

@interface TextIteratorsTest : XCTestCase

@end

@implementation TextIteratorsTest

- (void)setUp {
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testExample {
    // This is an example of a functional test case.
    // Use XCTAssert and related functions to verify your tests produce the correct results.
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
    }];
}

- (void)testWordIterator {
    WordIterator *iterator = [WordIterator new];
    [iterator update:@"sample test' , someone's coffee'cup walk into bar."];
    NSArray *array = @[@"sample", @"test'", @"someone's", @"coffee'cup", @"walk", @"into", @"bar"];
    SubString *subStr = [SubString new];
    for (NSInteger i = 0; i < array.count; i++) {
        [iterator nextWord:subStr];
        XCTAssertEqualObjects(subStr, array[i]);
    }
    XCTAssertFalse([iterator nextWord:subStr]);
    
    [iterator update:@"alice' in wonderland- ' -- word"];
    array = @[@"alice'", @"in", @"wonderland", @"word"];
    for (NSInteger i = 0; i < array.count; i++) {
        [iterator nextWord:subStr];
        XCTAssertEqualObjects(subStr, array[i]);
    }
    XCTAssertFalse([iterator nextWord:subStr]);
    
    [iterator update:@"我 not鸡 you speak 咩，please 重复again。a"];
    array = @[@"not", @"you", @"speak", @"please", @"again", @"a"];
    for (NSInteger i = 0; i < array.count; i++) {
        [iterator nextWord:subStr];
        XCTAssertEqualObjects(subStr, array[i]);
    }
    XCTAssertFalse([iterator nextWord:subStr]);
    
    [iterator update:@"This is a hyphen-split-test!, hope it work~ awesome!"];
    array = @[@"This", @"is", @"a", @"hyphen-split-test", @"hyphen", @"split", @"test", @"hope", @"it", @"work", @"awesome"];
    for (NSInteger i = 0; i < array.count; i++) {
        [iterator nextWord:subStr];
        XCTAssertEqualObjects(subStr, array[i]);
    }
    XCTAssertFalse([iterator nextWord:subStr]);
}

- (void)testWordGroupIterator {
    WordGroupIterator *iterator = [WordGroupIterator new];
    [iterator update:@"This is a hyphen-split-test!, hope it work~ awesome!"];
    SubString *subStr = [SubString new];
    NSArray *array = @[@"This is", @"This is a", @"This is a hyphen-split-test", @"This is a hyphen-split-test!, hope", @"This is a hyphen-split-test!, hope it",
                       @"is a", @"is a hyphen-split-test", @"is a hyphen-split-test!, hope", @"is a hyphen-split-test!, hope it",
                       @"is a hyphen-split-test!, hope it work", @"a hyphen-split-test", @"a hyphen-split-test!, hope", @"a hyphen-split-test!, hope it",
                       @"a hyphen-split-test!, hope it work", @"a hyphen-split-test!, hope it work~ awesome", @"hyphen-split-test!, hope",
                       @"hyphen-split-test!, hope it", @"hyphen-split-test!, hope it work", @"hyphen-split-test!, hope it work~ awesome", @"hope it",
                       @"hope it work", @"hope it work~ awesome", @"it work", @"it work~ awesome", @"work~ awesome"];
//    while ([iterator nextWordGroup:subStr]) {
//        NSLog(@"WORD:: %@", subStr);
//    }
    for (NSInteger i = 0; i < array.count; i++) {
        [iterator nextWordGroup:subStr];
        XCTAssertEqualObjects(subStr, array[i]);
    }
    XCTAssertFalse([iterator nextWordGroup:subStr]);
}

- (void)testSentenceIterator {
    SentenceIterator *iterator = [SentenceIterator new];
    [iterator update:@"adorn\nadorn sb. with sth.\nadorn sth. with sth.\nadrenal\nadrenal cortex"];
    SubString *str = [SubString new];
    while ([iterator nextSentence:str]) {
        NSLog(@">>>>> %@", str);
    }
}

@end
