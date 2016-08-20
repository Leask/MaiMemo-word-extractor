//
//  WordExtractorTest.m
//  WordExtractor
//
//  Created by TJT on 8/20/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "SubString.h"
#import "TextIterators.h"
#import "PhraseSearchTree.h"
#import "StringWrapper.h"
#import "WordExtractor.h"

@interface WordExtractorTest : XCTestCase

@end

@implementation WordExtractorTest

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
    NSString *s = [NSString stringWithContentsOfFile:@"/Users/TJT/all_vocs.txt" encoding:NSUTF8StringEncoding error:nil];
    NSString *text = [NSString stringWithContentsOfFile:@"/Users/TJT/alice30.txt" encoding:NSUTF8StringEncoding error:nil];
    NSArray<NSString *> *library = [s componentsSeparatedByString:@"\n"];
    WordExtractor *extractor = [[WordExtractor alloc] initWithLibrary:library];
    [self measureBlock:^{
       [extractor buildIndex];
        NSArray *arr = [extractor extract:text];
    }];
}

@end
