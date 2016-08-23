//
//  SubStringTest.m
//  WordExtractor
//
//  Created by TJT on 8/18/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import <XCTest/XCTest.h>
#import "SubString.h"

@interface SubStringTest : XCTestCase

@end

@implementation SubStringTest

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

- (void)testUpdate {
    SubString *str = [SubString new];
    [str updateWithString:@"Hello world" start:0 end:5];
    XCTAssertEqual(5, str.length);
    [str updateWithString:@"Hello world" start:0 end:0];
    XCTAssertEqual(0, str.length);
    @try {
        [str updateWithString:@"Hello world" start:0 end:120];
        [str updateWithString:@"Hello world" start:-1 end:4];
        [str updateWithString:@"Hello world" start:0 end:-2];
        [NSException raise:@"TestFailure" format:@""];
    }
    @catch (NSException *e) {
        if (![e.name isEqualToString:@"IndexOutOfBound"]) {
            @throw e;
        }
    }
}

- (void)testSubString {
    NSString *source = @"Hello world";
    SubString *str = [SubString new];
    [str updateWithString:source start:0 end:5];
    XCTAssertTrue([[str description] isEqualToString:@"Hello"]);
    XCTAssertEqual(5, str.length);
    [str updateWithString:source start:1 end:2];
    XCTAssertTrue([[str description] isEqualToString:@"e"]);
    XCTAssertEqual(1, str.length);
    [str updateWithString:source start:0 end:source.length];
    XCTAssertTrue([[str description] isEqualToString:source]);
    XCTAssertEqual(source.length, str.length);
    [str updateWithString:source start:0 end:0];
    XCTAssertTrue([[str description] isEqualToString:@""]);
    XCTAssertEqual(0, str.length);
    
    // test hash storage
}

- (void)testHash {
    NSString *source = @"Hello world";
    SubString *str = [SubString new];
    [str updateWithString:source start:0 end:5];
    SubString *containsTest = [SubString new];
    [containsTest updateWithString:@"world hello" start:6 end:11];
    NSMapTable *table = [[NSMapTable alloc] initWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory capacity:2];
    [table setObject:@"1" forKey:str];
    NSLog(@">>> %lu %@, %lu %@", str.hash, str, containsTest.hash, containsTest);
    NSSet *set = [[NSSet alloc] initWithObjects:str, nil];
    if ([table objectForKey:containsTest] == nil) {
        [NSException raise:@"TestFailure" format:@""];
    }
    [containsTest updateWithString:@"world hello" start:5 end:11];
    if ([set containsObject:containsTest]) {
        [NSException raise:@"TestFailure" format:@""];
    }
    if (![set containsObject:@"hello"]) {
        [NSException raise:@"TestFailure" format:@""];
    }
    if ([set containsObject:@YES]) {
        [NSException raise:@"TestFailure" format:@""];
    }
}

- (void)testCharAt {
    NSString *source = @"Hello world";
    SubString *str = [SubString new];
    [str updateWithString:source start:0 end:5];
    XCTAssertEqual('H', [str characterAtIndex:0]);
    XCTAssertEqual('e', [str characterAtIndex:1]);
    XCTAssertEqual('l', [str characterAtIndex:2]);
    XCTAssertEqual('l', [str characterAtIndex:3]);
    XCTAssertEqual('o', [str characterAtIndex:4]);
}

- (void)testSubString1 {
    NSString *source = @"Hello world";
    SubString *sub = [SubString new];
    [sub updateWithString:source start:0 end:source.length];
    XCTAssertEqualObjects([source substringToIndex:4], [sub substringToIndex:4]);
    XCTAssertEqualObjects([source substringFromIndex:4], [sub substringFromIndex:4]);
    XCTAssertEqualObjects([source substringWithRange:NSMakeRange(2, 4)], [sub substringWithRange:NSMakeRange(2, 4)]);
    [sub updateWithString:source start:6 end:source.length];
    XCTAssertEqualObjects(@"rld", [sub substringFromIndex:2]);
    XCTAssertEqualObjects(@"wor", [sub substringToIndex:3]);
    XCTAssertEqualObjects(@"orl", [sub substringWithRange:NSMakeRange(1, 3)]);
}

- (void)testPrefixSuffix {
    NSString *source = @"Hello world";
    SubString *sub = [SubString new];
    [sub updateWithString:source start:6 end:source.length];
    XCTAssertEqual(YES, [sub hasPrefix:@"wor"]);
    XCTAssertEqual(NO, [sub hasPrefix:@"or"]);
    XCTAssertEqual(YES, [sub hasSuffix:@"rld"]);
    XCTAssertEqual(NO, [sub hasSuffix:@"rl"]);
}

@end
