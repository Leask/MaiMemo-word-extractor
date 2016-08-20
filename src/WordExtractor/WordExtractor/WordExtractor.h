//
//  WordExtractor.h
//  WordExtractor
//
//  Created by TJT on 8/19/16.
//  Copyright © 2016 TJT. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Result : NSObject

@property (nonatomic, strong) NSString          *word;
@property (nonatomic, assign) NSInteger         index;

@end

@interface WordExtractor : NSObject

- (instancetype)initWithLibrary:(NSArray *)library;

- (void)buildIndex;

- (NSArray *)extract:(NSString *)text;

@end
